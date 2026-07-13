package com.kazemieh.rasteh.features.entity

import jakarta.persistence.*

/** حسابِ وفاداری (loyalty) — امتیاز و سطح به‌ازای هر کاربر. */
@Entity
@Table(name = "loyalty_accounts", uniqueConstraints = [UniqueConstraint(name = "uq_loyalty_user", columnNames = ["user_id"])])
class LoyaltyAccountEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,

    @Column(nullable = false)
    var points: Int = 0,

    @Column(nullable = false, length = 20)
    var tier: String = "BRONZE",
)
