package com.kazemieh.rasteh.identity.api.dto

import jakarta.validation.constraints.NotBlank

data class SendLoginOtpRequest(
    @field:NotBlank val mobile: String,
)
