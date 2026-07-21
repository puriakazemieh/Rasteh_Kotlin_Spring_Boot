package com.kazemieh.rasteh.engagement

import com.kazemieh.rasteh.engagement.entity.EventEntity
import com.kazemieh.rasteh.engagement.entity.ReferralEntity
import com.kazemieh.rasteh.engagement.entity.WarrantyEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EventRepository : JpaRepository<EventEntity, Long> {
    fun findAllByActiveTrueOrderByEventDateAsc(): List<EventEntity>
    fun findAllByActiveTrueAndLocationIdOrderByEventDateAsc(locationId: Long): List<EventEntity>
}

interface ReferralRepository : JpaRepository<ReferralEntity, Long> {
    fun findFirstByInviterUserIdAndInviteeUserIdIsNull(inviterUserId: Long): ReferralEntity?
    fun findFirstByCodeAndInviteeUserIdIsNull(code: String): ReferralEntity?
    fun findFirstByCode(code: String): ReferralEntity?
    fun countByInviterUserIdAndInviteeUserIdIsNotNull(inviterUserId: Long): Long
    fun existsByInviteeUserId(inviteeUserId: Long): Boolean
}

interface WarrantyRepository : JpaRepository<WarrantyEntity, Long> {
    fun findAllByUserIdOrderByIdDesc(userId: Long): List<WarrantyEntity>
}
