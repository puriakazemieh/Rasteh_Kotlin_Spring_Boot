package com.kazemieh.rasteh.engagement

import jakarta.validation.constraints.NotBlank
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
