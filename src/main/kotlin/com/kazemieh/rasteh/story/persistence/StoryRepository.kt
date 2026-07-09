package com.kazemieh.rasteh.story.persistence

import com.kazemieh.rasteh.story.persistence.entity.StoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface StoryRepository : JpaRepository<StoryEntity, Long> {
    fun findAllByIsActiveTrueAndExpiresAtAfterOrderByCreatedAtDesc(now: OffsetDateTime): List<StoryEntity>
}
