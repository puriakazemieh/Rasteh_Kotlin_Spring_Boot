package com.kazemieh.rasteh.features

import com.kazemieh.rasteh.features.entity.FlashSaleEntity
import com.kazemieh.rasteh.features.entity.GroupBuyEntity
import com.kazemieh.rasteh.features.entity.GroupBuyParticipantEntity
import com.kazemieh.rasteh.features.entity.LoyaltyAccountEntity
import com.kazemieh.rasteh.features.entity.PriceAlertEntity
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import com.kazemieh.rasteh.shared.error.ProductNotFoundException
import com.kazemieh.rasteh.shared.error.ShopAccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.OffsetDateTime

@Service
class FlashSaleService(
    private val repository: FlashSaleRepository,
    private val productRepository: ShopProductRepository,
) {
    private fun toResponse(f: FlashSaleEntity): FlashSaleResponse {
        val base = f.product?.price
        val sale = base?.multiply(BigDecimal(100 - f.discountPercent))?.divide(BigDecimal(100), 0, RoundingMode.HALF_UP)
        return FlashSaleResponse(
            id = f.id,
            productId = f.product?.id,
            productName = f.product?.name,
            shopId = f.product?.shop?.id,
            basePrice = base,
            discountPercent = f.discountPercent,
            salePrice = sale,
            startsAt = f.startsAt,
            endsAt = f.endsAt,
            stockLimit = f.stockLimit,
            soldCount = f.soldCount,
        )
    }

    @Transactional(readOnly = true)
    fun listActive(): List<FlashSaleResponse> =
        repository.findAllByActiveTrueOrderByEndsAtAsc()
            .filter { it.endsAt.isAfter(OffsetDateTime.now()) }
            .map(::toResponse)

    @Transactional
    fun create(userId: Long, isAdmin: Boolean, req: CreateFlashSaleRequest): FlashSaleResponse {
        val product = productRepository.findById(req.productId).orElseThrow { ProductNotFoundException() }
        if (!isAdmin && product.shop?.owner?.id != userId) throw ShopAccessDeniedException(product.shop?.id ?: 0)
        val now = OffsetDateTime.now()
        val flash = repository.save(
            FlashSaleEntity(
                product = product,
                discountPercent = req.discountPercent,
                startsAt = now,
                endsAt = now.plusHours(req.hours),
                stockLimit = req.stockLimit,
                active = true,
            )
        )
        return toResponse(flash)
    }
}

@Service
class GroupBuyService(
    private val repository: GroupBuyRepository,
    private val participantRepository: GroupBuyParticipantRepository,
    private val productRepository: ShopProductRepository,
) {
    private fun toResponse(g: GroupBuyEntity, joined: Boolean) = GroupBuyResponse(
        id = g.id,
        productId = g.product?.id,
        productName = g.product?.name,
        shopId = g.product?.shop?.id,
        unitPrice = g.unitPrice,
        targetCount = g.targetCount,
        currentCount = g.currentCount,
        endsAt = g.endsAt,
        joined = joined,
    )

    @Transactional(readOnly = true)
    fun listActive(userId: Long?): List<GroupBuyResponse> =
        repository.findAllByActiveTrueOrderByEndsAtAsc().map { g ->
            val joined = userId != null && participantRepository.findByGroupBuyIdAndUserId(g.id, userId) != null
            toResponse(g, joined)
        }

    @Transactional
    fun create(userId: Long, isAdmin: Boolean, req: CreateGroupBuyRequest): GroupBuyResponse {
        val product = productRepository.findById(req.productId).orElseThrow { ProductNotFoundException() }
        if (!isAdmin && product.shop?.owner?.id != userId) throw ShopAccessDeniedException(product.shop?.id ?: 0)
        val g = repository.save(
            GroupBuyEntity(
                product = product,
                unitPrice = req.unitPrice,
                targetCount = req.targetCount,
                endsAt = OffsetDateTime.now().plusDays(req.days),
                active = true,
            )
        )
        return toResponse(g, false)
    }

    @Transactional
    fun join(userId: Long, groupBuyId: Long): GroupBuyResponse {
        val g = repository.findById(groupBuyId).orElseThrow { ProductNotFoundException() }
        val existing = participantRepository.findByGroupBuyIdAndUserId(groupBuyId, userId)
        if (existing == null) {
            participantRepository.save(GroupBuyParticipantEntity(groupBuyId = groupBuyId, userId = userId))
            g.currentCount += 1
        }
        return toResponse(g, true)
    }
}

@Service
class LoyaltyService(
    private val repository: LoyaltyRepository,
) {
    private fun tierFor(points: Int): String = when {
        points >= 1000 -> "GOLD"
        points >= 300 -> "SILVER"
        else -> "BRONZE"
    }

    @Transactional
    fun getOrCreate(userId: Long): LoyaltyResponse {
        val account = repository.findByUserId(userId)
            ?: repository.save(LoyaltyAccountEntity(userId = userId, points = 0, tier = "BRONZE"))
        account.tier = tierFor(account.points)
        return LoyaltyResponse(points = account.points, tier = account.tier)
    }
}

@Service
class PriceAlertService(
    private val repository: PriceAlertRepository,
    private val productRepository: ShopProductRepository,
) {
    private fun toResponse(p: PriceAlertEntity) = PriceAlertResponse(
        id = p.id,
        productId = p.product?.id,
        productName = p.product?.name,
        targetPrice = p.targetPrice,
        currentPrice = p.product?.price,
        active = p.active,
        createdAt = p.createdAt,
    )

    @Transactional
    fun create(userId: Long, req: CreatePriceAlertRequest): PriceAlertResponse {
        val product = productRepository.findById(req.productId).orElseThrow { ProductNotFoundException() }
        return toResponse(
            repository.save(PriceAlertEntity(userId = userId, product = product, targetPrice = req.targetPrice, active = true))
        )
    }

    @Transactional(readOnly = true)
    fun listMine(userId: Long): List<PriceAlertResponse> =
        repository.findAllByUserIdOrderByIdDesc(userId).map(::toResponse)

    @Transactional
    fun delete(userId: Long, id: Long) {
        val alert = repository.findById(id).orElse(null) ?: return
        if (alert.userId == userId) repository.delete(alert)
    }
}
