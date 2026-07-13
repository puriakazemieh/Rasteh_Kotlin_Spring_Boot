package com.kazemieh.rasteh.features.entity

import com.kazemieh.rasteh.marketplace.persistence.entity.ShopProductEntity
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

/** خریدِ گروهی (groupbuy) — با رسیدن به ظرفیت، قیمتِ گروهی فعال می‌شود. */
@Entity
@Table(name = "group_buys")
class GroupBuyEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    var product: ShopProductEntity? = null,

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 0)
    var unitPrice: BigDecimal = BigDecimal.ZERO,

    @Column(name = "target_count", nullable = false)
    var targetCount: Int = 2,

    @Column(name = "current_count", nullable = false)
    var currentCount: Int = 0,

    @Column(name = "ends_at", nullable = false)
    var endsAt: OffsetDateTime = OffsetDateTime.now().plusDays(3),

    @Column(nullable = false)
    var active: Boolean = true,
)
