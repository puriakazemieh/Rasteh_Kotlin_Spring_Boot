package com.kazemieh.rasteh.marketplace.persistence

import com.kazemieh.rasteh.marketplace.persistence.entity.RastehEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RastehRepository : JpaRepository<RastehEntity, Long> {
    fun findAllByOrderBySortOrderAscIdAsc(): List<RastehEntity>
}
