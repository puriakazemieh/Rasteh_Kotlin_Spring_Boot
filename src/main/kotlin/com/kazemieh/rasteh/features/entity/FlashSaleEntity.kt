package com.kazemieh.rasteh.features.entity

import com.kazemieh.rasteh.marketplace.persistence.entity.ShopProductEntity
import jakarta.persistence.*
import java.time.OffsetDateTime

/** تخفیفِ ساعتیِ فلش روی یک کالا (flash). */
@Entity
@Table(name = "flash_sales")
class FlashSaleEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    var product: ShopProductEntity? = null,

    @Column(name = "discount_percent", nullable = false)
    var discountPercent: Int = 0,

    @Column(name = "starts_at", nullable = false)
    var startsAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "ends_at", nullable = false)
    var endsAt: OffsetDateTime = OffsetDateTime.now().plusHours(6),

    @Column(name = "stock_limit", nullable = false)
    var stockLimit: Int = 0,

    @Column(name = "sold_count", nullable = false)
    var soldCount: Int = 0,

    @Column(nullable = false)
    var active: Boolean = true,
)
