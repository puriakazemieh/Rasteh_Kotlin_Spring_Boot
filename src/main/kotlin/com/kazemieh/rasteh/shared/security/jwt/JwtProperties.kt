package com.kazemieh.rasteh.shared.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated

@ConfigurationProperties(prefix = "jwt")
@Validated
data class JwtProperties(
    @field:NotBlank(message = "JWT_SECRET_KEY must be configured")
    val secretKey: String,
    val issuer: String,
    val accessTtlMinutes: Long,
    val refreshTtlDays: Long,
)
