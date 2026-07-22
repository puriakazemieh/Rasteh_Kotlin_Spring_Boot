package com.kazemieh.rasteh.engagement.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

/** نشستِ لایوشاپینگ (live) — پخشِ زندهٔ یک فروشگاه با محصولِ پین‌شده. */
@Entity
@Table(name = "live_sessions")
class LiveSessionEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(name = "shop_id")
    var shopId: Long? = null,
    @Column(name = "shop_name", length = 120)
    var shopName: String? = null,
    @Column(nullable = false, length = 160)
    var title: String = "",
    @Column(nullable = false, length = 20)
    var status: String = "LIVE",
    @Column(name = "pinned_product_id")
    var pinnedProductId: Long? = null,
    @Column(name = "viewer_count", nullable = false)
    var viewerCount: Int = 0,
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,
)
