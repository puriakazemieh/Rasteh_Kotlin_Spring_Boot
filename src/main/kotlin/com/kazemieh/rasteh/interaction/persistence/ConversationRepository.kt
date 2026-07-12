package com.kazemieh.rasteh.interaction.persistence

import com.kazemieh.rasteh.interaction.persistence.entity.ConversationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ConversationRepository : JpaRepository<ConversationEntity, Long> {
    fun findByCustomerIdAndShopId(customerId: Long, shopId: Long): ConversationEntity?
    fun findAllByCustomerIdOrderByLastMessageAtDesc(customerId: Long): List<ConversationEntity>
    fun findAllByShopOwnerIdOrderByLastMessageAtDesc(ownerId: Long): List<ConversationEntity>
}
