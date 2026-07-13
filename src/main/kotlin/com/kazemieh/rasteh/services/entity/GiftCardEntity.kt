package com.kazemieh.rasteh.services.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.OffsetDateTime

/** کارتِ هدیه (giftcard) — کد + موجودی. */
@Entity
@Table(name = "gift_cards", uniqueConstraints = [UniqueConstraint(name = "uq_giftcard_code", columnNames = ["code"])])
class GiftCardEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(nullable = false, length = 20)
    var code: String = "",
    @Column(name = "initial_amount", nullable = false, precision = 15, scale = 0)
    var initialAmount: BigDecimal = BigDecimal.ZERO,
    @Column(nullable = false, precision = 15, scale = 0)
    var balance: BigDecimal = BigDecimal.ZERO,
    @Column(name = "owner_user_id")
    var ownerUserId: Long? = null,
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,
)
