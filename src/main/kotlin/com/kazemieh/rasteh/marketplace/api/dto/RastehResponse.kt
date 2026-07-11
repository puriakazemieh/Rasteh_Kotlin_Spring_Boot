package com.kazemieh.rasteh.marketplace.api.dto

data class RastehResponse(
    val id: Long,
    val label: String,
    val colorOklch: String?,
    val iconKey: String?,
    val sortOrder: Int,
)
