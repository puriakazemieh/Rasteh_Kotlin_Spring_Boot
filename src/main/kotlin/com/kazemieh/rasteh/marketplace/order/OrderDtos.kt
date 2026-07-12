package com.kazemieh.rasteh.marketplace.order

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.OffsetDateTime

data class OrderItemRequest(
    @field:NotNull val productId: Long,
    @field:NotNull @field:Min(1) val quantity: Int,
)

data class CreateOrderRequest(
    @field:NotNull val shopId: Long,
    @field:NotEmpty val items: List<OrderItemRequest>,
    val note: String? = null,
)

data class UpdateOrderStatusRequest(
    @field:NotNull val status: String,
)

data class OrderItemResponse(
    val id: Long,
    val productId: Long?,
    val productName: String,
    val unitPrice: BigDecimal,
    val quantity: Int,
    val lineTotal: BigDecimal,
)

data class OrderResponse(
    val id: Long,
    val shopId: Long?,
    val shopName: String?,
    val customerUserId: Long?,
    val customerName: String?,
    val status: String,
    val totalAmount: BigDecimal,
    val note: String?,
    val createdAt: OffsetDateTime?,
    val items: List<OrderItemResponse>,
)
