package com.kazemieh.rasteh.features

import com.kazemieh.rasteh.features.entity.LoyaltyAccountEntity
import org.springframework.data.jpa.repository.JpaRepository

interface LoyaltyRepository : JpaRepository<LoyaltyAccountEntity, Long> {
    fun findByUserId(userId: Long): LoyaltyAccountEntity?
}
