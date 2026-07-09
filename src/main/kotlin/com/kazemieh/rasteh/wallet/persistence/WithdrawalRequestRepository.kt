package com.kazemieh.rasteh.wallet.persistence

import com.kazemieh.rasteh.wallet.persistence.entity.WithdrawalRequestEntity
import com.kazemieh.rasteh.wallet.persistence.entity.WithdrawalStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WithdrawalRequestRepository : JpaRepository<WithdrawalRequestEntity, Long> {
    fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): List<WithdrawalRequestEntity>
    fun findAllByStatusOrderByCreatedAtAsc(status: WithdrawalStatus): List<WithdrawalRequestEntity>
}
