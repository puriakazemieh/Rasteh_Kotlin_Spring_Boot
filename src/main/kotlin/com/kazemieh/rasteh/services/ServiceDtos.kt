package com.kazemieh.rasteh.services

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.OffsetDateTime

// Appointment
data class CreateAppointmentRequest(
    @field:NotNull val shopId: Long,
    @field:NotNull val scheduledAt: OffsetDateTime,
    val note: String? = null,
)
data class AppointmentResponse(
    val id: Long, val shopId: Long?, val shopName: String?, val userId: Long,
    val scheduledAt: OffsetDateTime, val note: String?, val status: String, val createdAt: OffsetDateTime?,
)

// Gift card
data class CreateGiftCardRequest(
    @field:NotNull @field:Positive val amount: BigDecimal,
)
data class RedeemGiftCardRequest(
    @field:NotNull val code: String,
)
data class GiftCardResponse(
    val id: Long, val code: String, val initialAmount: BigDecimal, val balance: BigDecimal,
    val ownerUserId: Long?, val createdAt: OffsetDateTime?,
)

// Return
data class CreateReturnRequest(
    @field:NotNull val orderId: Long,
    val reason: String? = null,
)
data class ReturnResponse(
    val id: Long, val orderId: Long, val userId: Long, val reason: String?, val status: String, val createdAt: OffsetDateTime?,
)
