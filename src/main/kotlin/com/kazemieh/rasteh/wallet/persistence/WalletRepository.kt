package com.kazemieh.rasteh.wallet.persistence

import com.kazemieh.rasteh.wallet.persistence.entity.WalletEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import jakarta.persistence.LockModeType
import org.springframework.stereotype.Repository

@Repository
interface WalletRepository : JpaRepository<WalletEntity, Long> {
    fun findByUserId(userId: Long): WalletEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from WalletEntity w where w.user.id = :userId")
    fun findByUserIdForUpdate(@Param("userId") userId: Long): WalletEntity?
}
