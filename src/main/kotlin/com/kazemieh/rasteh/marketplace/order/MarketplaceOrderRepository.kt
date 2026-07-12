package com.kazemieh.rasteh.marketplace.order

import com.kazemieh.rasteh.marketplace.order.entity.MarketplaceOrderEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MarketplaceOrderRepository : JpaRepository<MarketplaceOrderEntity, Long> {
    fun findAllByCustomerIdOrderByIdDesc(customerId: Long): List<MarketplaceOrderEntity>
    fun findAllByShopIdOrderByIdDesc(shopId: Long): List<MarketplaceOrderEntity>
}
