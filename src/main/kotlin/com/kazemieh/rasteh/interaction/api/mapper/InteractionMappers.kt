package com.kazemieh.rasteh.interaction.api.mapper

import com.kazemieh.rasteh.interaction.api.dto.BookmarkResponse
import com.kazemieh.rasteh.interaction.api.dto.ConversationResponse
import com.kazemieh.rasteh.interaction.api.dto.MessageResponse
import com.kazemieh.rasteh.interaction.api.dto.OfferResponse
import com.kazemieh.rasteh.interaction.persistence.entity.BookmarkEntity
import com.kazemieh.rasteh.interaction.persistence.entity.ConversationEntity
import com.kazemieh.rasteh.interaction.persistence.entity.MessageEntity
import com.kazemieh.rasteh.interaction.persistence.entity.OfferEntity

private fun fullName(first: String?, last: String?): String? =
    listOfNotNull(first?.trim()?.ifBlank { null }, last?.trim()?.ifBlank { null })
        .joinToString(" ").ifBlank { null }

object ConversationMapper {
    fun toResponse(c: ConversationEntity) = ConversationResponse(
        id = c.id,
        shopId = c.shop?.id,
        shopName = c.shop?.name,
        shopEmoji = c.shop?.emoji,
        customerUserId = c.customer?.id,
        customerName = fullName(c.customer?.firstName, c.customer?.lastName),
        lastMessageAt = c.lastMessageAt,
        lastMessagePreview = c.lastMessagePreview,
    )
}

object MessageMapper {
    fun toResponse(m: MessageEntity) = MessageResponse(
        id = m.id,
        conversationId = m.conversation?.id,
        senderUserId = m.senderUserId,
        body = m.body,
        createdAt = m.createdAt,
    )
}

object OfferMapper {
    fun toResponse(o: OfferEntity) = OfferResponse(
        id = o.id,
        shopId = o.shop?.id,
        shopName = o.shop?.name,
        productId = o.product?.id,
        productName = o.product?.name,
        customerUserId = o.customer?.id,
        customerName = fullName(o.customer?.firstName, o.customer?.lastName),
        amount = o.amount,
        message = o.message,
        status = o.status.name,
        createdAt = o.createdAt,
    )
}

object BookmarkMapper {
    fun toResponse(b: BookmarkEntity) = BookmarkResponse(
        id = b.id,
        shopId = b.shop?.id,
        shopName = b.shop?.name,
        shopEmoji = b.shop?.emoji,
        productId = b.product?.id,
        productName = b.product?.name,
        createdAt = b.createdAt,
    )
}
