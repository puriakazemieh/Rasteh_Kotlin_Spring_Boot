package com.kazemieh.rasteh.advanced.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

/** اعلانِ کاربر (تأییدِ ونـدور، سفارش، پیشنهاد، هشدارِ قیمت، پاسخِ چت). */
@Entity
@Table(name = "notifications")
class NotificationEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = 0,
    @Column(name = "user_id", nullable = false) var userId: Long = 0,
    @Column(nullable = false, length = 160) var title: String = "",
    @Column(columnDefinition = "text") var body: String? = null,
    @Column(nullable = false) var read: Boolean = false,
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) var createdAt: OffsetDateTime? = null,
)
