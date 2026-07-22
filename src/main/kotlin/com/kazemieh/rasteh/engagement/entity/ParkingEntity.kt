package com.kazemieh.rasteh.engagement.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.OffsetDateTime

/** نشستِ پارکینگِ کاربر (parking) — جای پارک، زمانِ ورود/خروج، هزینه، پرداخت. */
@Entity
@Table(name = "parking_sessions")
class ParkingEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,
    @Column(nullable = false, length = 40)
    var spot: String = "",
    @Column(name = "entered_at", nullable = false)
    var enteredAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "exited_at")
    var exitedAt: OffsetDateTime? = null,
    @Column(nullable = false, precision = 15, scale = 0)
    var fee: BigDecimal = BigDecimal.ZERO,
    @Column(nullable = false)
    var paid: Boolean = false,
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,
)
