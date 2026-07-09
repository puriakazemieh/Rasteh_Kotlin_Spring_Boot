package com.kazemieh.rasteh.identity.api.dto


data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse,
)