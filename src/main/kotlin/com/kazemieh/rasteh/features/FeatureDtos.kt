package com.kazemieh.rasteh.features

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.OffsetDateTime

// ---- Flash ----
data class CreateFlashSaleRequest(
    @field:NotNull val productId: Long,
    @field:NotNull val discountPercent: Int,
    val hours: Long = 6,
    val stockLimit: Int = 0,
)
data class FlashSaleResponse(
    val id: Long,
    val productId: Long?,
    val productName: String?,
    val shopId: Long?,
    val basePrice: BigDecimal?,
    val discountPercent: Int,
    val salePrice: BigDecimal?,
    val startsAt: OffsetDateTime,
    val endsAt: OffsetDateTime,
    val stockLimit: Int,
    val soldCount: Int,
)

// ---- Group buy ----
data class CreateGroupBuyRequest(
    @field:NotNull val productId: Long,
    @field:NotNull @field:Positive val unitPrice: BigDecimal,
    val targetCount: Int = 2,
    val days: Long = 3,
)
data class GroupBuyResponse(
    val id: Long,
    val productId: Long?,
    val productName: String?,
    val shopId: Long?,
    val unitPrice: BigDecimal,
    val targetCount: Int,
    val currentCount: Int,
    val endsAt: OffsetDateTime,
    val joined: Boolean,
)

// ---- Loyalty ----
data class LoyaltyResponse(
    val points: Int,
    val tier: String,
)

// ---- Price alert ----
data class CreatePriceAlertRequest(
    @field:NotNull val productId: Long,
    @field:NotNull @field:Positive val targetPrice: BigDecimal,
)
data class PriceAlertResponse(
    val id: Long,
    val productId: Long?,
    val productName: String?,
    val targetPrice: BigDecimal,
    val currentPrice: BigDecimal?,
    val active: Boolean,
    val createdAt: OffsetDateTime?,
)
