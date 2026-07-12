package com.kazemieh.rasteh.marketplace.api.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.OffsetDateTime

data class CreateReviewRequest(
    @field:NotNull val shopId: Long,
    @field:NotNull @field:Min(1) @field:Max(5) val rating: Int,
    val comment: String? = null,
)

data class ReviewResponse(
    val id: Long,
    val shopId: Long?,
    val userId: Long,
    val authorName: String?,
    val rating: Int,
    val comment: String?,
    val createdAt: OffsetDateTime?,
)
