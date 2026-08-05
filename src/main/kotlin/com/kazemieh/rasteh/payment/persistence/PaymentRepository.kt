package com.kazemieh.rasteh.payment.persistence

import com.kazemieh.rasteh.payment.persistence.entity.PaymentEntity
import com.kazemieh.rasteh.payment.persistence.entity.PaymentStatus
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.OffsetDateTime

interface PaymentRepository : JpaRepository<PaymentEntity, Long> {
    fun findByAuthority(authority: String): PaymentEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PaymentEntity p WHERE p.userId = :userId AND p.idempotencyKey = :idempotencyKey")
    fun findByUserIdAndIdempotencyKeyForUpdate(
        @Param("userId") userId: Long,
        @Param("idempotencyKey") idempotencyKey: String,
    ): PaymentEntity?

    /**
     * Callback verification is a financial state transition.  Serialising
     * consumers of the same PSP authority prevents two concurrent callbacks
     * from both observing PENDING and applying an order or wallet effect.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PaymentEntity p WHERE p.authority = :authority")
    fun findByAuthorityForUpdate(@Param("authority") authority: String): PaymentEntity?

    fun findAllByOrderId(orderId: Long): List<PaymentEntity>

    // متد جدید برای پیدا کردن پرداخت‌های در انتظار بررسی
    @Query("SELECT p FROM PaymentEntity p WHERE p.status = :status AND p.createdAt < :expirationTime")
    fun findUnverifiedPayments(
        @Param("status") status: PaymentStatus,
        @Param("expirationTime") expirationTime: OffsetDateTime
    ): List<PaymentEntity>
}
