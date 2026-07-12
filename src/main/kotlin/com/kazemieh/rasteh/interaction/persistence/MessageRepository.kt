package com.kazemieh.rasteh.interaction.persistence

import com.kazemieh.rasteh.interaction.persistence.entity.MessageEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository : JpaRepository<MessageEntity, Long> {
    fun findAllByConversationIdOrderByIdAsc(conversationId: Long): List<MessageEntity>
}
