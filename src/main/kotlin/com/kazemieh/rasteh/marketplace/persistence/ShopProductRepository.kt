package com.kazemieh.rasteh.marketplace.persistence

import com.kazemieh.rasteh.marketplace.persistence.entity.ShopProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ShopProductRepository : JpaRepository<ShopProductEntity, Long>, JpaSpecificationExecutor<ShopProductEntity> {
    fun findAllByShopIdAndActiveTrueOrderByIdDesc(shopId: Long): List<ShopProductEntity>
    fun findAllByShopIdOrderByIdDesc(shopId: Long): List<ShopProductEntity>
}
