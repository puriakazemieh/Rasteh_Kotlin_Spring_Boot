package com.kazemieh.rasteh.marketplace.order

import com.kazemieh.rasteh.marketplace.order.entity.MarketplaceOrderEntity
import com.kazemieh.rasteh.marketplace.order.entity.MarketplaceOrderItemEntity

object OrderMapper {
    private fun itemToResponse(i: MarketplaceOrderItemEntity) = OrderItemResponse(
        id = i.id,
        productId = i.product?.id,
        productName = i.productName,
        unitPrice = i.unitPrice,
        quantity = i.quantity,
        lineTotal = i.lineTotal,
    )

    fun toResponse(o: MarketplaceOrderEntity): OrderResponse {
        val customer = o.customer
        val customerName = customer?.let {
            listOfNotNull(it.firstName?.trim(), it.lastName?.trim()).joinToString(" ").ifBlank { null }
        }
        return OrderResponse(
            id = o.id,
            shopId = o.shop?.id,
            shopName = o.shop?.name,
            customerUserId = customer?.id,
            customerName = customerName,
            status = o.status.name,
            totalAmount = o.totalAmount,
            note = o.note,
            createdAt = o.createdAt,
            items = o.items.map(::itemToResponse),
        )
    }
}
