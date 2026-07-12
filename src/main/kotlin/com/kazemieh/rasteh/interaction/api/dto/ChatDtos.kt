package com.kazemieh.rasteh.interaction.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.OffsetDateTime

data class StartConversationRequest(
    @field:NotNull val shopId: Long,
)

data class SendMessageRequest(
    @field:NotBlank val body: String,
)

data class ConversationResponse(
    val id: Long,
    val shopId: Long?,
    val shopName: String?,
    val shopEmoji: String?,
    val customerUserId: Long?,
    val customerName: String?,
    val lastMessageAt: OffsetDateTime?,
    val lastMessagePreview: String?,
)

data class MessageResponse(
    val id: Long,
    val conversationId: Long?,
    val senderUserId: Long,
    val body: String,
    val createdAt: OffsetDateTime?,
)
