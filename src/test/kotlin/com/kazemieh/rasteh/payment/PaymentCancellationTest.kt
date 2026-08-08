package com.kazemieh.rasteh.payment

import com.kazemieh.rasteh.order.application.OrderService
import com.kazemieh.rasteh.payment.application.PaymentService
import com.kazemieh.rasteh.payment.application.ZarinPalService
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

class PaymentCancellationTest {
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
    fun `cancelled provider return does not clear cart or verify PSP`() {
        val payment = PaymentEntity(
            orderId = 13L,
            amount = BigDecimal("125000"),
            authority = "authority-cancelled",
            status = PaymentStatus.PENDING,
        )
        every { payments.findByAuthorityForUpdate("authority-cancelled") } returns payment
        every { payments.save(any()) } answers { firstArg() }

        assertThat(service.verifyPayment("authority-cancelled", "NOK")).isFalse()
        assertThat(payment.status).isEqualTo(PaymentStatus.FAILED)
        verify(exactly = 0) { gateway.verifyPayment(any(), any()) }
        verify(exactly = 0) { orders.clearCartAfterSuccessfulPayment(any()) }
    }
}
