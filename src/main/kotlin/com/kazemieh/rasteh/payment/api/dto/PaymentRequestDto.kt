package com.kazemieh.rasteh.payment.api.dto

data class PaymentRequestDto(
    val orderId: String,
    val idempotencyKey: String,
)
