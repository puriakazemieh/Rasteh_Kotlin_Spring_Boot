package com.kazemieh.rasteh.marketplace.persistence

import com.kazemieh.rasteh.marketplace.domain.ShopStatus
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ShopRepository : JpaRepository<ShopEntity, Long> {
    fun findAllByOwnerIdOrderByCreatedAtDesc(ownerId: Long): List<ShopEntity>
    fun findAllByStatusOrderByCreatedAtDesc(status: ShopStatus): List<ShopEntity>
    fun findAllByLocationIdAndStatusOrderByRatingDesc(locationId: Long, status: ShopStatus): List<ShopEntity>
    fun findAllByLocationIdAndRastehIdAndStatusOrderByRatingDesc(locationId: Long, rastehId: Long, status: ShopStatus): List<ShopEntity>
    fun countByOwnerId(ownerId: Long): Long
}
