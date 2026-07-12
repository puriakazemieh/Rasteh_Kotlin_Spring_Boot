package com.kazemieh.rasteh.interaction.api.dto

import java.time.OffsetDateTime

data class CreateBookmarkRequest(
    val shopId: Long? = null,
    val productId: Long? = null,
)

data class BookmarkResponse(
    val id: Long,
    val shopId: Long?,
    val shopName: String?,
    val shopEmoji: String?,
    val productId: Long?,
    val productName: String?,
    val createdAt: OffsetDateTime?,
)
