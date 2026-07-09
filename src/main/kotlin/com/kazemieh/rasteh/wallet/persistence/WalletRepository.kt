package com.kazemieh.rasteh.wallet.persistence

import com.kazemieh.rasteh.wallet.persistence.entity.WalletEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WalletRepository : JpaRepository<WalletEntity, Long> {
    fun findByUserId(userId: Long): WalletEntity?
}
