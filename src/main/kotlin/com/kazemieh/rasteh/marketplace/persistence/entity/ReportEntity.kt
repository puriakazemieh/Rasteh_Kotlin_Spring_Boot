package com.kazemieh.rasteh.marketplace.persistence.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

/** گزارشِ تخلف روی فروشگاه یا محصول. */
@Entity
@Table(name = "reports")
class ReportEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,
    @Column(name = "target_type", nullable = false, length = 20)
    var targetType: String = "SHOP",   // SHOP / PRODUCT
    @Column(name = "target_id", nullable = false)
    var targetId: Long = 0,
    @Column(columnDefinition = "text")
    var reason: String? = null,
    @Column(nullable = false, length = 20)
    var status: String = "OPEN",       // OPEN / RESOLVED / DISMISSED
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,
)
