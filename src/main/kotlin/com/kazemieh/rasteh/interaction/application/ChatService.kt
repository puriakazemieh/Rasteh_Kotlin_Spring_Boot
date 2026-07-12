package com.kazemieh.rasteh.interaction.application

import com.kazemieh.rasteh.identity.application.exception.UserNotFoundException
import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.interaction.api.dto.ConversationResponse
import com.kazemieh.rasteh.interaction.api.dto.MessageResponse
import com.kazemieh.rasteh.interaction.api.mapper.ConversationMapper
import com.kazemieh.rasteh.interaction.api.mapper.MessageMapper
import com.kazemieh.rasteh.interaction.persistence.ConversationRepository
import com.kazemieh.rasteh.interaction.persistence.MessageRepository
import com.kazemieh.rasteh.interaction.persistence.entity.ConversationEntity
import com.kazemieh.rasteh.interaction.persistence.entity.MessageEntity
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.shared.error.ChatAccessDeniedException
import com.kazemieh.rasteh.shared.error.ConversationNotFoundException
import com.kazemieh.rasteh.shared.error.ShopNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class ChatService(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val shopRepository: ShopRepository,
    private val userRepository: UserRepository,
) {

    /** خریدار گفت‌وگو با فروشگاه را می‌سازد یا برمی‌گرداند. */
    @Transactional
    fun startOrGet(userId: Long, shopId: Long): ConversationResponse {
        val existing = conversationRepository.findByCustomerIdAndShopId(userId, shopId)
        if (existing != null) return ConversationMapper.toResponse(existing)

        val shop = shopRepository.findById(shopId).orElseThrow { ShopNotFoundException(shopId) }
        val customer = userRepository.findById(userId).orElseThrow { UserNotFoundException() }
        val conversation = conversationRepository.save(
            ConversationEntity(customer = customer, shop = shop, lastMessageAt = OffsetDateTime.now())
        )
        return ConversationMapper.toResponse(conversation)
    }

    /** گفت‌وگوهایِ من — هم به‌عنوانِ خریدار و هم به‌عنوانِ فروشندهٔ فروشگاه. */
    @Transactional(readOnly = true)
    fun listMine(userId: Long): List<ConversationResponse> {
        val asCustomer = conversationRepository.findAllByCustomerIdOrderByLastMessageAtDesc(userId)
        val asVendor = conversationRepository.findAllByShopOwnerIdOrderByLastMessageAtDesc(userId)
        return (asCustomer + asVendor)
            .distinctBy { it.id }
            .sortedByDescending { it.lastMessageAt }
            .map(ConversationMapper::toResponse)
    }

    @Transactional(readOnly = true)
    fun listMessages(userId: Long, isAdmin: Boolean, conversationId: Long): List<MessageResponse> {
        requireParticipant(conversationId, userId, isAdmin)
        return messageRepository.findAllByConversationIdOrderByIdAsc(conversationId).map(MessageMapper::toResponse)
    }

    @Transactional
    fun sendMessage(userId: Long, isAdmin: Boolean, conversationId: Long, body: String): MessageResponse {
        val conversation = requireParticipant(conversationId, userId, isAdmin)
        val trimmed = body.trim()
        val message = messageRepository.save(
            MessageEntity(conversation = conversation, senderUserId = userId, body = trimmed)
        )
        conversation.lastMessageAt = OffsetDateTime.now()
        conversation.lastMessagePreview = trimmed.take(200)
        return MessageMapper.toResponse(message)
    }

    private fun requireParticipant(conversationId: Long, userId: Long, isAdmin: Boolean): ConversationEntity {
        val conversation = conversationRepository.findById(conversationId)
            .orElseThrow { ConversationNotFoundException(conversationId) }
        val isCustomer = conversation.customer?.id == userId
        val isVendor = conversation.shop?.owner?.id == userId
        if (!isAdmin && !isCustomer && !isVendor) throw ChatAccessDeniedException(conversationId)
        return conversation
    }
}
