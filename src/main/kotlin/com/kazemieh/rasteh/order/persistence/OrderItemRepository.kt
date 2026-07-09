package com.kazemieh.rasteh.order.persistence

import com.kazemieh.rasteh.order.persistence.entity.OrderItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderItemRepository : JpaRepository<OrderItemEntity, Long> {
    fun existsByVariantId(variantId: Long): Boolean
}
