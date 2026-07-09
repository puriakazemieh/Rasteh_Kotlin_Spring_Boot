package com.kazemieh.rasteh.identity.api.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank val username: String, // Can be email or mobile
    @field:NotBlank val password: String,
)
