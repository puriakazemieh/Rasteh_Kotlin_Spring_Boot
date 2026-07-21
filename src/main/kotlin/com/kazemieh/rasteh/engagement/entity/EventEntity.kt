package com.kazemieh.rasteh.engagement.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

/** رویداد/جشنوارهٔ یک محل (events) — عنوان، توضیح، تاریخِ برگزاری. */
@Entity
@Table(name = "events")
class EventEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(name = "location_id")
    var locationId: Long? = null,
    @Column(nullable = false, length = 160)
    var title: String = "",
    @Column(columnDefinition = "text")
    var description: String? = null,
    @Column(name = "event_date", nullable = false)
    var eventDate: OffsetDateTime = OffsetDateTime.now(),
    @Column(nullable = false)
    var active: Boolean = true,
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,
)
