package com.kazemieh.rasteh.catalog.api.dto

import jakarta.validation.constraints.NotBlank

data class AdminAddVideoRequest(
    @field:NotBlank val url: String,
    val sortOrder: Int? = null
)
