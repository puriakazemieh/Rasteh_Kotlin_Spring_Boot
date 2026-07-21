package com.kazemieh.rasteh.engagement

import com.kazemieh.rasteh.engagement.entity.EventEntity
import com.kazemieh.rasteh.engagement.entity.ReferralEntity
import com.kazemieh.rasteh.engagement.entity.WarrantyEntity
import com.kazemieh.rasteh.interaction.persistence.BookmarkRepository
import com.kazemieh.rasteh.marketplace.order.MarketplaceOrderRepository
import com.kazemieh.rasteh.shared.error.BadRequestException
import com.kazemieh.rasteh.shared.error.ErrorCodes
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Service
class EventService(private val repository: EventRepository) {
    private fun toResponse(e: EventEntity) = EventResponse(e.id, e.locationId, e.title, e.description, e.eventDate, e.createdAt)

    @Transactional(readOnly = true)
    fun list(locationId: Long?): List<EventResponse> =
        (if (locationId != null) repository.findAllByActiveTrueAndLocationIdOrderByEventDateAsc(locationId)
        else repository.findAllByActiveTrueOrderByEventDateAsc()).map(::toResponse)
}

@Service
class ReferralService(private val repository: ReferralRepository) {
    private val codePool = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    private fun genCode(): String = buildString { repeat(8) { append(codePool[Random.nextInt(codePool.length)]) } }

    @Transactional
    fun myReferral(userId: Long): ReferralResponse {
        val owner = repository.findFirstByInviterUserIdAndInviteeUserIdIsNull(userId)
            ?: repository.save(ReferralEntity(inviterUserId = userId, code = uniqueCode()))
        val count = repository.countByInviterUserIdAndInviteeUserIdIsNotNull(userId)
        return ReferralResponse(owner.code, count, if (count > 0) "REWARDED" else "PENDING")
    }

    private fun uniqueCode(): String {
        var code = genCode()
        while (repository.findFirstByCode(code) != null) code = genCode()
        return code
    }

    @Transactional
    fun redeem(userId: Long, req: RedeemReferralRequest): ReferralResponse {
        val code = req.code.trim().uppercase()
        val owner = repository.findFirstByCodeAndInviteeUserIdIsNull(code)
            ?: throw BadRequestException("Referral code not found", ErrorCodes.INVALID_INPUT)
        if (owner.inviterUserId == userId) throw BadRequestException("Cannot use your own code", ErrorCodes.INVALID_INPUT)
        if (repository.existsByInviteeUserId(userId)) throw BadRequestException("Already invited", ErrorCodes.INVALID_INPUT)
        repository.save(ReferralEntity(inviterUserId = owner.inviterUserId, code = owner.code, inviteeUserId = userId, rewardStatus = "REWARDED"))
        // خروجی: وضعیتِ دعوتِ خودِ کاربر (کدِ خودش را هم می‌سازد اگر ندارد)
        return myReferral(userId)
    }
}

@Service
class WarrantyService(private val repository: WarrantyRepository) {
    private fun toResponse(w: WarrantyEntity) = WarrantyResponse(w.id, w.title, w.serial, w.validUntil, w.createdAt)

    @Transactional
    fun create(userId: Long, req: CreateWarrantyRequest): WarrantyResponse =
        toResponse(repository.save(WarrantyEntity(userId = userId, title = req.title.trim(), serial = req.serial?.trim(), validUntil = req.validUntil)))

    @Transactional(readOnly = true)
    fun listMine(userId: Long): List<WarrantyResponse> = repository.findAllByUserIdOrderByIdDesc(userId).map(::toResponse)
}

@Service
class ActivityService(
    private val bookmarkRepository: BookmarkRepository,
    private val orderRepository: MarketplaceOrderRepository,
) {
    /** فیدِ فعالیت: تجمیعِ نشان‌کردن‌ها و سفارش‌های اخیرِ کاربر (بدونِ جدولِ اضافه). */
    @Transactional(readOnly = true)
    fun feed(userId: Long): List<ActivityItemResponse> {
        val saves = bookmarkRepository.findAllByUserIdOrderByIdDesc(userId).map { b ->
            val name = b.shop?.name ?: b.product?.title ?: "مورد"
            ActivityItemResponse("SAVE", "نشان‌کردنِ «$name»", null, b.createdAt)
        }
        val orders = orderRepository.findAllByCustomerIdOrderByIdDesc(userId).map { o ->
            ActivityItemResponse("ORDER", "سفارش از «${o.shop?.name ?: "فروشگاه"}»", o.totalAmount.toPlainString(), o.createdAt)
        }
        return (saves + orders).sortedByDescending { it.createdAt }.take(50)
    }
}
