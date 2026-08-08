package com.kazemieh.rasteh.payment

import com.kazemieh.rasteh.order.application.OrderService
import com.kazemieh.rasteh.order.persistence.entity.OrderStatus
import com.kazemieh.rasteh.payment.application.PaymentService
import com.kazemieh.rasteh.payment.application.ZarinPalService
import com.kazemieh.rasteh.payment.application.ZarinPalVerificationResponse
import com.kazemieh.rasteh.payment.persistence.PaymentRepository
import com.kazemieh.rasteh.payment.persistence.entity.PaymentEntity
import com.kazemieh.rasteh.payment.persistence.entity.PaymentStatus
import com.kazemieh.rasteh.wallet.application.WalletService
import com.kazemieh.rasteh.wallet.persistence.WalletTransactionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PaymentReplayProtectionTest {
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
    fun `replayed successful callback has one provider verification and one order effect`() {
        val payment = PaymentEntity(
            id = 7,
            orderId = 13,
            amount = BigDecimal("125000"),
            authority = "authority-7",
            status = PaymentStatus.PENDING,
        )
        every { payments.findByAuthorityForUpdate("authority-7") } returns payment
        every { payments.save(any()) } answers { firstArg() }
        every { gateway.verifyPayment("authority-7", 125000L) } returns
            ZarinPalVerificationResponse(isSuccess = true, refId = "ref-7")

        assertThat(service.verifyPayment("authority-7", "OK")).isTrue()
        assertThat(service.verifyPayment("authority-7", "OK")).isTrue()

        verify(exactly = 1) { gateway.verifyPayment("authority-7", 125000L) }
        verify(exactly = 1) { orders.updateStatus(13, OrderStatus.PROCESSING) }
        verify(exactly = 1) { orders.clearCartAfterSuccessfulPayment(13) }
    }
}
