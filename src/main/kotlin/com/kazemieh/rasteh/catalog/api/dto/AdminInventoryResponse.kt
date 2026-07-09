package com.kazemieh.rasteh.catalog.api.dto

import java.time.OffsetDateTime

data class AdminInventoryResponse(
    val variantId: Long,
    val onHand: Int,
    val reserved: Int,
    val available: Int,
    val version: Int,
    val updatedAt: OffsetDateTime?
)