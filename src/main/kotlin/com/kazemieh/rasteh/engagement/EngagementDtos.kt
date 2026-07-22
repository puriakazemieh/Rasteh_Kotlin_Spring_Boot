package com.kazemieh.rasteh.engagement

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.OffsetDateTime

data class EventResponse(
    val id: Long,
    val locationId: Long?,
    val title: String,
    val description: String?,
    val eventDate: OffsetDateTime,
    val createdAt: OffsetDateTime?,
)

data class ReferralResponse(
    val code: String,
    val invitedCount: Long,
    val rewardStatus: String,
)

data class RedeemReferralRequest(
    @field:NotBlank val code: String,
)

data class WarrantyResponse(
    val id: Long,
    val title: String,
    val serial: String?,
    val validUntil: OffsetDateTime?,
    val createdAt: OffsetDateTime?,
)

data class CreateWarrantyRequest(
    @field:NotBlank val title: String,
    val serial: String? = null,
    val validUntil: OffsetDateTime? = null,
)

data class ActivityItemResponse(
    val type: String,      // SAVE | ORDER
    val title: String,
    val subtitle: String?,
    val createdAt: OffsetDateTime?,
)

data class ParkingResponse(
    val id: Long,
    val spot: String,
    val enteredAt: OffsetDateTime,
    val exitedAt: OffsetDateTime?,
    val fee: java.math.BigDecimal,
    val paid: Boolean,
)

data class CheckinParkingRequest(
    @field:NotBlank val spot: String,
)

data class LiveSessionResponse(
    val id: Long,
    val shopId: Long?,
    val shopName: String?,
    val title: String,
    val status: String,
    val pinnedProductId: Long?,
    val viewerCount: Int,
)

data class EscrowResponse(
    val id: Long,
    val orderId: Long?,
    val amount: java.math.BigDecimal,
    val status: String,
    val releasedAt: OffsetDateTime?,
    val createdAt: OffsetDateTime?,
)

data class CreateEscrowRequest(
    @field:NotNull val amount: java.math.BigDecimal,
    val orderId: Long? = null,
)
