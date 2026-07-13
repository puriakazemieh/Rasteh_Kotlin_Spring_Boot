package com.kazemieh.rasteh.services.entity

import com.kazemieh.rasteh.marketplace.persistence.entity.ShopEntity
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

/** رزروِ بازدید از فروشگاه (appointment). */
@Entity
@Table(name = "appointments")
class AppointmentEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    var shop: ShopEntity? = null,
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,
    @Column(name = "scheduled_at", nullable = false)
    var scheduledAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(columnDefinition = "text")
    var note: String? = null,
    @Column(nullable = false, length = 20)
    var status: String = "REQUESTED",   // REQUESTED / CONFIRMED / CANCELLED / DONE
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,
)
