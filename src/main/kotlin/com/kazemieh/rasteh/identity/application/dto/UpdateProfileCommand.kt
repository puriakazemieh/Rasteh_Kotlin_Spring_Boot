package com.kazemieh.rasteh.identity.application.dto

data class UpdateProfileCommand(
    val fullName: String?,
    val phone: String?
)