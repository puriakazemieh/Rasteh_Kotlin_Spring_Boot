package com.kazemieh.rasteh.marketplace.api.mapper

import com.kazemieh.rasteh.marketplace.api.dto.ProductResponse
import com.kazemieh.rasteh.marketplace.domain.ShopType
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopProductEntity

object ProductMapper {
    fun toResponse(p: ShopProductEntity): ProductResponse {
        val shop = p.shop
        val purchasable = shop?.type == ShopType.BUYABLE && p.active && p.stock > 0
        return ProductResponse(
            id = p.id,
            shopId = shop?.id,
            shopName = shop?.name,
            name = p.name,
            description = p.description,
            price = p.price,
            oldPrice = p.oldPrice,
            discountPercent = p.discountPercent,
            condition = p.condition.name,
            stock = p.stock,
            categoryName = p.categoryName,
            emoji = p.emoji,
            imageUrl = p.imageUrl,
            active = p.active,
            purchasable = purchasable,
            createdAt = p.createdAt,
        )
    }
}
