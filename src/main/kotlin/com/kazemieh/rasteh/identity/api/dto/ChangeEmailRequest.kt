package com.kazemieh.rasteh.identity.api.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class ChangeEmailRequest(
    @field:Email @field:NotBlank
    val email: String,
)