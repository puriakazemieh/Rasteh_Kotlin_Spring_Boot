package com.kazemieh.rasteh.shared.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secretKey: String,
    val issuer: String,
    val accessTtlMinutes: Long,
    val refreshTtlDays: Long,
)