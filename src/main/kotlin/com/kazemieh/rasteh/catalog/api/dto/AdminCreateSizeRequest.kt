package com.kazemieh.rasteh.catalog.api.dto

import jakarta.validation.constraints.NotBlank

data class AdminCreateSizeRequest(
    @field:NotBlank val name: String,
    val sortOrder: Int = 0
)
