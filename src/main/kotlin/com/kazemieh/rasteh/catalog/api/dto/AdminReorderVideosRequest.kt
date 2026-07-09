package com.kazemieh.rasteh.catalog.api.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty

data class AdminReorderVideosRequest(
    @field:NotEmpty @field:Valid
    val items: List<AdminImageOrderItem>
)
