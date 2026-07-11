package com.kazemieh.rasteh.marketplace.application

import com.kazemieh.rasteh.identity.domain.UserRole
import com.kazemieh.rasteh.marketplace.api.mapper.ShopMapper
import com.kazemieh.rasteh.marketplace.domain.ShopStatus
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.shared.error.ShopNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class AdminShopService(
    private val shopRepository: ShopRepository,
) {

    /** صفِ تأیید — پیش‌فرض PENDING. */
    @Transactional(readOnly = true)
    fun listByStatus(status: ShopStatus) =
        shopRepository.findAllByStatusOrderByCreatedAtDesc(status).map(ShopMapper::toResponse)

    @Transactional
    fun approve(shopId: Long): com.kazemieh.rasteh.marketplace.api.dto.ShopResponse {
        val shop = shopRepository.findById(shopId).orElseThrow { ShopNotFoundException(shopId) }
        shop.status = ShopStatus.APPROVED
        shop.approvedAt = OffsetDateTime.now()
        // اعطای نقشِ VENDOR به مالک پس از تأیید (اگر هنوز مشتری است).
        shop.owner?.let { if (it.role == UserRole.CUSTOMER) it.role = UserRole.VENDOR }
        return ShopMapper.toResponse(shop)
    }

    @Transactional
    fun reject(shopId: Long): com.kazemieh.rasteh.marketplace.api.dto.ShopResponse {
        val shop = shopRepository.findById(shopId).orElseThrow { ShopNotFoundException(shopId) }
        shop.status = ShopStatus.REJECTED
        return ShopMapper.toResponse(shop)
    }

    @Transactional
    fun suspend(shopId: Long): com.kazemieh.rasteh.marketplace.api.dto.ShopResponse {
        val shop = shopRepository.findById(shopId).orElseThrow { ShopNotFoundException(shopId) }
        shop.status = ShopStatus.SUSPENDED
        return ShopMapper.toResponse(shop)
    }
}
