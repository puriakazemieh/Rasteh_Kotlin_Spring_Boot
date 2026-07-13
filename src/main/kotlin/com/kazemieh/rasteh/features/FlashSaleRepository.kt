package com.kazemieh.rasteh.features

import com.kazemieh.rasteh.features.entity.FlashSaleEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FlashSaleRepository : JpaRepository<FlashSaleEntity, Long> {
    fun findAllByActiveTrueOrderByEndsAtAsc(): List<FlashSaleEntity>
}
