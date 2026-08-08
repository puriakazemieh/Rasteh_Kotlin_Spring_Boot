package com.kazemieh.rasteh.payment

import com.kazemieh.rasteh.order.application.OrderService
import com.kazemieh.rasteh.payment.application.PaymentService
import com.kazemieh.rasteh.payment.application.ZarinPalService
import com.kazemieh.rasteh.payment.persistence.PaymentRepository
import com.kazemieh.rasteh.wallet.application.WalletService
import com.kazemieh.rasteh.wallet.persistence.WalletTransactionRepository
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PaymentFeatureFlagTest {
    private val service = PaymentService(
        mockk<ZarinPalService>(relaxed = true),
        mockk<PaymentRepository>(relaxed = true),
        mockk<OrderService>(relaxed = true),
        mockk<WalletService>(relaxed = true),
        mockk<WalletTransactionRepository>(relaxed = true),
        false
    )

    @Test
    fun `disabled payment callback has no financial effect`() {
        assertThat(service.verifyPayment("forged-authority", "OK")).isFalse()
    }
}
