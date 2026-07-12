package com.kazemieh.rasteh.interaction.api.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.OffsetDateTime

data class CreateOfferRequest(
    @field:NotNull val shopId: Long,
    val productId: Long? = null,
    @field:NotNull @field:Positive val amount: BigDecimal,
    val message: String? = null,
)

data class OfferResponse(
    val id: Long,
    val shopId: Long?,
    val shopName: String?,
    val productId: Long?,
    val productName: String?,
    val customerUserId: Long?,
    val customerName: String?,
    val amount: BigDecimal,
    val message: String?,
    val status: String,
    val createdAt: OffsetDateTime?,
)
