package com.kazemieh.rasteh.engagement.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

/** دفترچهٔ ضمانتِ دیجیتال (warranty) — عنوانِ کالا، شمارهٔ سریال، تاریخِ اعتبار. */
@Entity
@Table(name = "warranties")
class WarrantyEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,
    @Column(nullable = false, length = 160)
    var title: String = "",
    @Column(length = 80)
    var serial: String? = null,
    @Column(name = "valid_until")
    var validUntil: OffsetDateTime? = null,
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,
)
