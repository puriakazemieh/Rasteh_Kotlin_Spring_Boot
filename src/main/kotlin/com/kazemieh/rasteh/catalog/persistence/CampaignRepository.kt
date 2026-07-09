package com.kazemieh.rasteh.catalog.persistence

import com.kazemieh.rasteh.catalog.persistence.entity.CampaignEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface CampaignRepository : JpaRepository<CampaignEntity, Long> {
    /** سوزان‌ترین کمپینِ فعالی که هنوز به پایان نرسیده. */
    fun findFirstByIsActiveTrueAndEndsAtAfterOrderByEndsAtAsc(now: OffsetDateTime): CampaignEntity?

    fun findAllByOrderByCreatedAtDesc(): List<CampaignEntity>
}
