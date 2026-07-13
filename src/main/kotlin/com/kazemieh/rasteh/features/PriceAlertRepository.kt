package com.kazemieh.rasteh.features

import com.kazemieh.rasteh.features.entity.PriceAlertEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PriceAlertRepository : JpaRepository<PriceAlertEntity, Long> {
    fun findAllByUserIdOrderByIdDesc(userId: Long): List<PriceAlertEntity>
}
