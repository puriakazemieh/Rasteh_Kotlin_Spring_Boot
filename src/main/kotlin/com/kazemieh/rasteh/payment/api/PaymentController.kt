package com.kazemieh.rasteh.payment.api

import com.kazemieh.rasteh.order.application.OrderService
import com.kazemieh.rasteh.payment.api.dto.PaymentRequestDto
import com.kazemieh.rasteh.payment.api.dto.PaymentResponseDto
import com.kazemieh.rasteh.payment.application.PaymentService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payment")
class PaymentController(
    private val paymentService: PaymentService,
    private val orderService: OrderService
) {

    @PostMapping("/request")
    fun requestPayment(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestBody request: PaymentRequestDto,
    ): ResponseEntity<PaymentResponseDto> {
        val orderIdLong = request.orderId.toLongOrNull()
            ?: throw IllegalArgumentException("Invalid Order ID format")

        val order = orderService.getMyOrder(principal.id, orderIdLong)
        val amount = order.gatewayPaidAmount

        val paymentUrl = paymentService.startPayment(
            orderId = orderIdLong,
            amount = amount,
            userId = principal.id,
            idempotencyKey = request.idempotencyKey,
        )
        
        return if (paymentUrl != null) {
            ResponseEntity.ok(PaymentResponseDto(paymentUrl))
        } else {
             ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/callback")
    fun handleCallback(
        @RequestParam(value = "Authority", required = false) authority: String?,
        @RequestParam(value = "Status", required = false) status: String?,
        response: HttpServletResponse
    ) {
        if (authority != null && status != null) {
            val isSuccess = paymentService.verifyPayment(authority, status)
            
            if (isSuccess) {
                response.sendRedirect("myapp://payment-result?status=success")
                return
            }
        }
        
        response.sendRedirect("myapp://payment-result?status=failed")
    }
}
