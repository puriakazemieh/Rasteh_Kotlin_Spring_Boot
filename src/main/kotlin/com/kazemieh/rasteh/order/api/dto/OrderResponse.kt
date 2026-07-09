package com.kazemieh.rasteh.order.api.dto

import java.math.BigDecimal
import java.time.OffsetDateTime

data class OrderResponse(
    val id: Long,
    val status: String,
    val subtotalPrice: BigDecimal,
    val shippingPrice: BigDecimal,
    val totalPrice: BigDecimal,
    val createdAt: OffsetDateTime?,
)
