package com.kazemieh.rasteh.marketplace.api.dto

import java.math.BigDecimal
import java.time.OffsetDateTime

data class ShopResponse(
    val id: Long,
    val locationId: Long?,
    val locationName: String?,
    val rastehId: Long?,
    val rastehLabel: String?,
    val ownerUserId: Long?,
    val name: String,
    val category: String?,
    val floor: String?,
    val type: String,
    val verified: Boolean,
    val rating: BigDecimal,
    val reviewsCount: Int,
    val salesCount: Int,
    val phone: String?,
    val hasChat: Boolean,
    val acceptsOffers: Boolean,
    val about: String?,
    val workingHoursJson: String?,
    val address: String?,
    val mapX: Double?,
    val mapY: Double?,
    val emoji: String?,
    val coverStyle: String?,
    val coverUrl: String?,
    val logoUrl: String?,
    val status: String,
    val createdAt: OffsetDateTime?,
    val approvedAt: OffsetDateTime?,
)
