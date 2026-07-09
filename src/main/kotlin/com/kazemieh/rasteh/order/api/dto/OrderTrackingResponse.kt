package com.kazemieh.rasteh.order.api.dto

import com.kazemieh.rasteh.order.persistence.entity.OrderStatus
import java.time.Instant

data class OrderTrackingResponse(
    val id: Int,
    val status: OrderStatus,
    val trackingCode: String?,
    val orderedAt: Instant,
    val shippedAt: Instant?
)
