package com.kazemieh.rasteh.payment

import com.kazemieh.rasteh.order.application.OrderService
import com.kazemieh.rasteh.payment.application.PaymentService
import com.kazemieh.rasteh.payment.application.ZarinPalService
import com.kazemieh.rasteh.payment.persistence.PaymentRepository
import com.kazemieh.rasteh.wallet.application.WalletService
import com.kazemieh.rasteh.wallet.persistence.WalletTransactionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PaymentStartIdempotencyTest {
    private val gateway = mockk<ZarinPalService>()
    private val payments = mockk<PaymentRepository>()
    private val orders = mockk<OrderService>(relaxed = true)
    private val service = PaymentService(
        gateway,
        payments,
        orders,
        mockk<WalletService>(relaxed = true),
        mockk<WalletTransactionRepository>(relaxed = true),
        true,
    )

    @Test
    fun `same checkout request key creates one PSP payment attempt`() {
        var stored: com.kazemieh.rasteh.payment.persistence.entity.PaymentEntity? = null
        every { payments.findByUserIdAndIdempotencyKeyForUpdate(7L, "checkout-42-unique") } answers { stored }
        every { payments.save(any()) } answers {
            firstArg<com.kazemieh.rasteh.payment.persistence.entity.PaymentEntity>().also { stored = it }
        }
        every { gateway.createPaymentRequest(125000L, "42") } returns "https://sandbox.zarinpal.com/pg/StartPay/authority-42"
        every { gateway.startPayUrlFor("authority-42") } returns "https://sandbox.zarinpal.com/pg/StartPay/authority-42"

        val first = service.startPayment(
            orderId = 42L,
            amount = BigDecimal("125000"),
            userId = 7L,
            idempotencyKey = "checkout-42-unique",
        )
        val second = service.startPayment(
            orderId = 42L,
            amount = BigDecimal("125000"),
            userId = 7L,
            idempotencyKey = "checkout-42-unique",
        )

        assertThat(second).isEqualTo(first)
        verify(exactly = 1) { gateway.createPaymentRequest(125000L, "42") }
        verify(exactly = 2) { orders.lockMyOrderForPayment(7L, 42L) }
    }
}
