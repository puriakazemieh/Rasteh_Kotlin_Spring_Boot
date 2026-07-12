package com.kazemieh.rasteh.marketplace.api.dto

import java.math.BigDecimal
import java.time.OffsetDateTime

data class ProductResponse(
    val id: Long,
    val shopId: Long?,
    val shopName: String?,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val oldPrice: BigDecimal?,
    val discountPercent: Int?,
    val condition: String,
    val stock: Int,
    val categoryName: String?,
    val emoji: String?,
    val imageUrl: String?,
    val active: Boolean,
    val purchasable: Boolean,
    val createdAt: OffsetDateTime?,
)
