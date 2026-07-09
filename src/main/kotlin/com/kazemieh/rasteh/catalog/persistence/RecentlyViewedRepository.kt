package com.kazemieh.rasteh.catalog.persistence

import com.kazemieh.rasteh.catalog.persistence.entity.RecentlyViewedEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecentlyViewedRepository : JpaRepository<RecentlyViewedEntity, Long> {
    fun findByUserIdAndProductId(userId: Long, productId: Long): RecentlyViewedEntity?
    fun findAllByUserIdOrderByViewedAtDesc(userId: Long, pageable: Pageable): Page<RecentlyViewedEntity>
    fun deleteAllByProductId(productId: Long)
}
