package com.kazemieh.rasteh.services.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

/** درخواستِ مرجوعی روی یک سفارش (returns). */
@Entity
@Table(name = "return_requests")
class ReturnRequestEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(name = "order_id", nullable = false)
    var orderId: Long = 0,
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,
    @Column(columnDefinition = "text")
    var reason: String? = null,
    @Column(nullable = false, length = 20)
    var status: String = "REQUESTED",   // REQUESTED / APPROVED / REJECTED / REFUNDED
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,
)
