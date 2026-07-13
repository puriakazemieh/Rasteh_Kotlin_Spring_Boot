package com.kazemieh.rasteh.services

import com.kazemieh.rasteh.services.entity.AppointmentEntity
import com.kazemieh.rasteh.services.entity.GiftCardEntity
import com.kazemieh.rasteh.services.entity.ReturnRequestEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AppointmentRepository : JpaRepository<AppointmentEntity, Long> {
    fun findAllByUserIdOrderByIdDesc(userId: Long): List<AppointmentEntity>
    fun findAllByShopIdOrderByIdDesc(shopId: Long): List<AppointmentEntity>
}

interface GiftCardRepository : JpaRepository<GiftCardEntity, Long> {
    fun findByCode(code: String): GiftCardEntity?
    fun findAllByOwnerUserIdOrderByIdDesc(ownerUserId: Long): List<GiftCardEntity>
}

interface ReturnRequestRepository : JpaRepository<ReturnRequestEntity, Long> {
    fun findAllByUserIdOrderByIdDesc(userId: Long): List<ReturnRequestEntity>
}
