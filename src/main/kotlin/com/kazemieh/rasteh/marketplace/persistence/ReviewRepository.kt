package com.kazemieh.rasteh.marketplace.persistence

import com.kazemieh.rasteh.marketplace.persistence.entity.ReviewEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<ReviewEntity, Long> {
    fun findAllByShopIdOrderByIdDesc(shopId: Long): List<ReviewEntity>
    fun findByUserIdAndShopId(userId: Long, shopId: Long): ReviewEntity?
}
