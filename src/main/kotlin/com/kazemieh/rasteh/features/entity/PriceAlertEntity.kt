package com.kazemieh.rasteh.features.entity

import com.kazemieh.rasteh.marketplace.persistence.entity.ShopProductEntity
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.OffsetDateTime

/** هشدارِ قیمت (pricealert) — وقتی قیمتِ کالا به هدف رسید. */
@Entity
@Table(name = "price_alerts")
class PriceAlertEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    var product: ShopProductEntity? = null,

    @Column(name = "target_price", nullable = false, precision = 15, scale = 0)
    var targetPrice: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    var active: Boolean = true,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,
)
