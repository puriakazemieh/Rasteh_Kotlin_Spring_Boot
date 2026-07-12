package com.kazemieh.rasteh.marketplace.order.entity

import com.kazemieh.rasteh.marketplace.persistence.entity.ShopProductEntity
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "marketplace_order_items")
class MarketplaceOrderItemEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    var order: MarketplaceOrderEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: ShopProductEntity? = null,

    @Column(name = "product_name", nullable = false, length = 200)
    var productName: String = "",

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 0)
    var unitPrice: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    var quantity: Int = 1,

    @Column(name = "line_total", nullable = false, precision = 15, scale = 0)
    var lineTotal: BigDecimal = BigDecimal.ZERO,
)
