package com.kazemieh.rasteh.payment

import com.kazemieh.rasteh.order.application.OrderService
import com.kazemieh.rasteh.order.persistence.OrderRepository
import com.kazemieh.rasteh.payment.application.PaymentService
import com.kazemieh.rasteh.payment.application.TransactionCleanupService
import com.kazemieh.rasteh.payment.application.ZarinPalService
import com.kazemieh.rasteh.payment.persistence.PaymentRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class TransactionCleanupServiceFeatureFlagTest {
    private val zarinPalService = mockk<ZarinPalService>(relaxed = true)
    private val service = TransactionCleanupService(
        mockk<OrderRepository>(relaxed = true),
        mockk<OrderService>(relaxed = true),
        mockk<PaymentRepository>(relaxed = true),
        mockk<PaymentService>(relaxed = true),
        zarinPalService,
        false
    )

    @Test
    fun `disabled payments never call the PSP recovery endpoint`() {
        service.verifyPendingPaymentsInZarinpal()

        verify(exactly = 0) { zarinPalService.getUnverifiedTransactions() }
    }
}
