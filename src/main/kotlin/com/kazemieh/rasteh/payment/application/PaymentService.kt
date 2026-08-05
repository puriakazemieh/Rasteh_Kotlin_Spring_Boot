package com.kazemieh.rasteh.payment.application

import com.kazemieh.rasteh.order.application.OrderService
import com.kazemieh.rasteh.order.persistence.entity.OrderStatus
import com.kazemieh.rasteh.payment.persistence.PaymentRepository
import com.kazemieh.rasteh.payment.persistence.entity.PaymentEntity
import com.kazemieh.rasteh.payment.persistence.entity.PaymentStatus
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class PaymentService(
    private val zarinPalService: ZarinPalService,
    private val paymentRepository: PaymentRepository,
    private val orderService: OrderService,
    private val walletService: com.kazemieh.rasteh.wallet.application.WalletService,
    private val transactionRepository: com.kazemieh.rasteh.wallet.persistence.WalletTransactionRepository,
    @Value("\${app.payment.enabled:false}") private val paymentEnabled: Boolean
) {

    @Transactional
    fun startPayment(
        orderId: Long?,
        amount: BigDecimal,
        userId: Long,
        walletTransactionId: Long? = null,
        idempotencyKey: String,
    ): String? {
        if (!paymentEnabled) {
            throw ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Payments are disabled")
        }
        if (idempotencyKey.isBlank() || idempotencyKey.length > 100) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid payment idempotency key is required")
        }

        // A row lock on the owned order also covers the no-row-yet case for a
        // new payment attempt, so two concurrent checkout requests cannot both
        // reach the PSP before the idempotency record exists.
        if (orderId != null) {
            orderService.lockMyOrderForPayment(userId, orderId)
        }

        paymentRepository.findByUserIdAndIdempotencyKeyForUpdate(userId, idempotencyKey)?.let { existing ->
            if (
                existing.orderId != orderId ||
                existing.walletTransactionId != walletTransactionId ||
                existing.amount.compareTo(amount) != 0
            ) {
                throw ResponseStatusException(HttpStatus.CONFLICT, "Payment idempotency key conflicts with another request")
            }
            return existing.authority.takeIf(String::isNotBlank)?.let(zarinPalService::startPayUrlFor)
        }

        val payment = PaymentEntity(
            orderId = orderId,
            walletTransactionId = walletTransactionId,
            userId = userId,
            idempotencyKey = idempotencyKey,
            amount = amount,
            status = PaymentStatus.PENDING,
            authority = ""
        )
        val savedPayment = paymentRepository.save(payment)

        val paymentUrl = zarinPalService.createPaymentRequest(
            amount.toIrrMinorUnits(),
            (orderId ?: "wallet_$walletTransactionId").toString()
        )

        if (paymentUrl != null) {
            val authority = paymentUrl.substringAfterLast("/")
            savedPayment.authority = authority
            paymentRepository.save(savedPayment)
        }

        return paymentUrl
    }

    @Transactional
    fun verifyPayment(authority: String, status: String): Boolean {
        if (!paymentEnabled) return false
        // The database lock, rather than the pre-lock status check, is the
        // idempotency boundary for concurrent provider callbacks.
        val payment = paymentRepository.findByAuthorityForUpdate(authority)
            ?: return false // پرداخت پیدا نشد

        // 4. جلوگیری از پردازش تکراری (Idempotency)
        if (payment.status != PaymentStatus.PENDING) {
            // این تراکنش قبلاً پردازش شده است. فقط نتیجه قبلی را برمی‌گردانیم.
            return payment.status == PaymentStatus.SUCCESS
        }

        // 2. عدم کنسل کردن سفارش در صورت انصراف کاربر
        if (status != "OK") {
            payment.status = PaymentStatus.FAILED
            paymentRepository.save(payment)
            // دیگر سفارش را کنسل نمی‌کنیم. کاربر می‌تواند دوباره تلاش کند.
            return false
        }

        val verificationResponse = zarinPalService.verifyPayment(authority, payment.amount.toIrrMinorUnits())

        if (verificationResponse.isSuccess) {
            payment.status = PaymentStatus.SUCCESS
            payment.refId = verificationResponse.refId
            paymentRepository.save(payment)

            if (payment.orderId != null) {
                orderService.updateStatus(payment.orderId!!, OrderStatus.PROCESSING)
                orderService.clearCartAfterSuccessfulPayment(payment.orderId!!)
            } else if (payment.walletTransactionId != null) {
                walletService.confirmTransaction(payment.walletTransactionId!!, payment.refId ?: "")
            }

            return true
        } else {
            payment.status = PaymentStatus.FAILED
            paymentRepository.save(payment)
            // در صورت شکست در وریفای هم سفارش را کنسل نمی‌کنیم.
            return false
        }
    }

    private fun BigDecimal.toIrrMinorUnits(): Long = try {
        toBigIntegerExact().longValueExact()
    } catch (_: ArithmeticException) {
        throw ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Payment amount must be an integer IRR minor unit"
        )
    }
}
