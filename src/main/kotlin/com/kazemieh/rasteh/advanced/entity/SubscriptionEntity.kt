package com.kazemieh.rasteh.advanced.entity

import jakarta.persistence.*
import java.time.OffsetDateTime

/** اشتراکِ «بازارچه پلاس» (vipsub). */
@Entity
@Table(name = "subscriptions", uniqueConstraints = [UniqueConstraint(name = "uq_subscription_user", columnNames = ["user_id"])])
class SubscriptionEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = 0,
    @Column(name = "user_id", nullable = false) var userId: Long = 0,
    @Column(nullable = false, length = 20) var plan: String = "PLUS",
    @Column(nullable = false) var active: Boolean = false,
    @Column(name = "expires_at") var expiresAt: OffsetDateTime? = null,
)
