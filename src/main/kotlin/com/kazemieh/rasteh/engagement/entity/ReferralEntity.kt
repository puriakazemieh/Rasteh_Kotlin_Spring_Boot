package com.kazemieh.rasteh.engagement.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

/**
 * دعوتِ دوستان (referral) — هر کاربر یک کدِ دعوتِ یکتا دارد؛ دعوت‌شونده با ثبتِ کد به آن وصل می‌شود.
 * برای سادگی، هر ردیف یک «کدِ مالک» است و دعوت‌شونده‌ها جداگانه شمارش می‌شوند.
 */
@Entity
@Table(name = "referrals")
class ReferralEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(name = "inviter_user_id", nullable = false)
    var inviterUserId: Long = 0,
    @Column(nullable = false, length = 16)
    var code: String = "",
    @Column(name = "invitee_user_id")
    var inviteeUserId: Long? = null,
    @Column(name = "reward_status", nullable = false, length = 20)
    var rewardStatus: String = "PENDING",
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,
)
