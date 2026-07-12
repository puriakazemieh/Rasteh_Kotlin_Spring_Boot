package com.kazemieh.rasteh.interaction.persistence

import com.kazemieh.rasteh.interaction.persistence.entity.OfferEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OfferRepository : JpaRepository<OfferEntity, Long> {
    fun findAllByCustomerIdOrderByIdDesc(customerId: Long): List<OfferEntity>
    fun findAllByShopIdOrderByIdDesc(shopId: Long): List<OfferEntity>
}
