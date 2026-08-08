package com.kazemieh.rasteh.payment

import com.kazemieh.rasteh.order.api.dto.OrderDetailResponse
import com.kazemieh.rasteh.order.application.OrderService
import com.kazemieh.rasteh.payment.api.PaymentController
import com.kazemieh.rasteh.payment.api.dto.PaymentRequestDto
import com.kazemieh.rasteh.payment.application.PaymentService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PaymentGatewayRemainderTest {
    private val orders = mockk<OrderService>()
    private val payments = mockk<PaymentService>()
    private val controller = PaymentController(payments, orders)

    @Test
    fun `partial wallet payment sends only the unpaid remainder to the gateway`() {
        val order = mockk<OrderDetailResponse>()
        every { order.gatewayPaidAmount } returns BigDecimal("75000")
        every { orders.getMyOrder(7L, 42L) } returns order
        every { payments.startPayment(42L, BigDecimal("75000"), 7L, null, "checkout-42") } returns "https://sandbox.zarinpal.com/pg/StartPay/example"

        val response = controller.requestPayment(
            UserPrincipal(7L, "test-user", "not-used", "USER", true),
            PaymentRequestDto(orderId = "42", idempotencyKey = "checkout-42"),
        )

        assertThat(response.body?.paymentUrl).contains("StartPay")
        verify(exactly = 1) {
            payments.startPayment(42L, BigDecimal("75000"), 7L, null, "checkout-42")
        }
        verify(exactly = 0) {
            payments.startPayment(42L, BigDecimal("125000"), 7L, any(), any())
        }
    }
}
