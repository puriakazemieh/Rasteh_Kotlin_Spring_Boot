package com.kazemieh.rasteh.discount.persistence

import com.kazemieh.rasteh.discount.persistence.entity.DiscountEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface DiscountRepository : JpaRepository<DiscountEntity, Long> {
    fun findByCode(code: String): Optional<DiscountEntity>
}
