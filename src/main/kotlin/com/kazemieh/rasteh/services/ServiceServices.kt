package com.kazemieh.rasteh.services

import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.services.entity.AppointmentEntity
import com.kazemieh.rasteh.services.entity.GiftCardEntity
import com.kazemieh.rasteh.services.entity.ReturnRequestEntity
import com.kazemieh.rasteh.shared.error.BadRequestException
import com.kazemieh.rasteh.shared.error.ErrorCodes
import com.kazemieh.rasteh.shared.error.ShopNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Service
class AppointmentService(
    private val repository: AppointmentRepository,
    private val shopRepository: ShopRepository,
) {
    private fun toResponse(a: AppointmentEntity) = AppointmentResponse(
        a.id, a.shop?.id, a.shop?.name, a.userId, a.scheduledAt, a.note, a.status, a.createdAt,
    )

    @Transactional
    fun book(userId: Long, req: CreateAppointmentRequest): AppointmentResponse {
        val shop = shopRepository.findById(req.shopId).orElseThrow { ShopNotFoundException(req.shopId) }
        return toResponse(repository.save(AppointmentEntity(shop = shop, userId = userId, scheduledAt = req.scheduledAt, note = req.note?.trim())))
    }

    @Transactional(readOnly = true)
    fun listMine(userId: Long) = repository.findAllByUserIdOrderByIdDesc(userId).map(::toResponse)
}

@Service
class GiftCardService(
    private val repository: GiftCardRepository,
) {
    private fun toResponse(g: GiftCardEntity) = GiftCardResponse(g.id, g.code, g.initialAmount, g.balance, g.ownerUserId, g.createdAt)

    private val codePool = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ"
    private fun genCode(): String = buildString { repeat(12) { append(codePool[Random.nextInt(codePool.length)]) } }

    @Transactional
    fun issue(userId: Long, req: CreateGiftCardRequest): GiftCardResponse {
        var code = genCode()
        while (repository.findByCode(code) != null) code = genCode()
        return toResponse(repository.save(GiftCardEntity(code = code, initialAmount = req.amount, balance = req.amount, ownerUserId = userId)))
    }

    @Transactional
    fun redeem(userId: Long, req: RedeemGiftCardRequest): GiftCardResponse {
        val card = repository.findByCode(req.code.trim().uppercase())
            ?: throw BadRequestException("Gift card not found", ErrorCodes.INVALID_INPUT)
        card.ownerUserId = userId
        return toResponse(card)
    }

    @Transactional(readOnly = true)
    fun listMine(userId: Long) = repository.findAllByOwnerUserIdOrderByIdDesc(userId).map(::toResponse)
}

@Service
class ReturnRequestService(
    private val repository: ReturnRequestRepository,
) {
    private fun toResponse(r: ReturnRequestEntity) = ReturnResponse(r.id, r.orderId, r.userId, r.reason, r.status, r.createdAt)

    @Transactional
    fun create(userId: Long, req: CreateReturnRequest): ReturnResponse =
        toResponse(repository.save(ReturnRequestEntity(orderId = req.orderId, userId = userId, reason = req.reason?.trim())))

    @Transactional(readOnly = true)
    fun listMine(userId: Long) = repository.findAllByUserIdOrderByIdDesc(userId).map(::toResponse)
}
