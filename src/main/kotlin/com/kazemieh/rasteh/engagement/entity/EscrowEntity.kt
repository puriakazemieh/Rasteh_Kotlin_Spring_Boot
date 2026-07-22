package com.kazemieh.rasteh.engagement.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.OffsetDateTime

/** پرداختِ امانی (escrow) — نگه‌داریِ مبلغ تا تأییدِ دریافت توسطِ خریدار. */
@Entity
@Table(name = "escrows")
class EscrowEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,
    @Column(name = "order_id")
    var orderId: Long? = null,
    @Column(nullable = false, precision = 15, scale = 0)
    var amount: BigDecimal = BigDecimal.ZERO,
    @Column(nullable = false, length = 20)
    var status: String = "HELD",
    @Column(name = "released_at")
    var releasedAt: OffsetDateTime? = null,
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,
)
