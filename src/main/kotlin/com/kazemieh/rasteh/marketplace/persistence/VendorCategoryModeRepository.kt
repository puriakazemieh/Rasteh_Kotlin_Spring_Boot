package com.kazemieh.rasteh.marketplace.persistence

import com.kazemieh.rasteh.marketplace.persistence.entity.VendorCategoryModeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface VendorCategoryModeRepository : JpaRepository<VendorCategoryModeEntity, Long> {
    fun findAllByShopId(shopId: Long): List<VendorCategoryModeEntity>
}
