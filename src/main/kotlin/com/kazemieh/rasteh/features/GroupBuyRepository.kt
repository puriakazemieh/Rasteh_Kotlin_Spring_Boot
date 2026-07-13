package com.kazemieh.rasteh.features

import com.kazemieh.rasteh.features.entity.GroupBuyEntity
import com.kazemieh.rasteh.features.entity.GroupBuyParticipantEntity
import org.springframework.data.jpa.repository.JpaRepository

interface GroupBuyRepository : JpaRepository<GroupBuyEntity, Long> {
    fun findAllByActiveTrueOrderByEndsAtAsc(): List<GroupBuyEntity>
}

interface GroupBuyParticipantRepository : JpaRepository<GroupBuyParticipantEntity, Long> {
    fun findByGroupBuyIdAndUserId(groupBuyId: Long, userId: Long): GroupBuyParticipantEntity?
}
