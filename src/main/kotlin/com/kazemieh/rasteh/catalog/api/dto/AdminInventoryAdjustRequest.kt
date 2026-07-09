package com.kazemieh.rasteh.catalog.api.dto

data class AdminInventoryAdjustRequest(
    val delta: Int,
    val version: Int? = null
)