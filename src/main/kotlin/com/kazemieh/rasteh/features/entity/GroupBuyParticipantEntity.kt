package com.kazemieh.rasteh.features.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

@Entity
@Table(
    name = "group_buy_participants",
    uniqueConstraints = [UniqueConstraint(name = "uq_groupbuy_user", columnNames = ["group_buy_id", "user_id"])]
)
class GroupBuyParticipantEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "group_buy_id", nullable = false)
    var groupBuyId: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,
)
