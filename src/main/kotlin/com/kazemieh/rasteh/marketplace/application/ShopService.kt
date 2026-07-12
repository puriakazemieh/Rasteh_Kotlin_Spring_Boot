package com.kazemieh.rasteh.marketplace.application

import com.kazemieh.rasteh.identity.application.exception.UserNotFoundException
import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.marketplace.api.dto.CreateShopRequest
import com.kazemieh.rasteh.marketplace.api.dto.ShopResponse
import com.kazemieh.rasteh.marketplace.api.mapper.ShopMapper
import com.kazemieh.rasteh.marketplace.domain.ShopStatus
import com.kazemieh.rasteh.marketplace.persistence.LocationRepository
import com.kazemieh.rasteh.marketplace.persistence.RastehRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopEntity
import com.kazemieh.rasteh.shared.error.LocationNotFoundException
import com.kazemieh.rasteh.shared.error.RastehNotFoundException
import com.kazemieh.rasteh.shared.error.ShopNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ShopService(
    private val shopRepository: ShopRepository,
    private val userRepository: UserRepository,
    private val rastehRepository: RastehRepository,
    private val locationRepository: LocationRepository,
) {

    /** ثبتِ درخواستِ فروشگاه توسطِ کاربر (becomeVendor). وضعیتِ اولیه PENDING. */
    @Transactional
    fun register(userId: Long, req: CreateShopRequest): ShopResponse {
        val user = userRepository.findById(userId).orElseThrow { UserNotFoundException() }

        val rasteh = req.rastehId?.let {
            rastehRepository.findById(it).orElseThrow { RastehNotFoundException(it) }
        }
        val location = req.locationId?.let {
            locationRepository.findById(it).orElseThrow { LocationNotFoundException(it) }
        }

        val shop = ShopEntity(
            owner = user,
            rasteh = rasteh,
            location = location,
            name = req.name.trim(),
            category = req.category?.trim(),
            floor = req.floor?.trim(),
            type = req.type,
            phone = req.phone?.trim(),
            address = req.address?.trim(),
            workingHoursJson = req.workingHoursJson,
            about = req.about?.trim(),
            hasChat = req.hasChat,
            acceptsOffers = req.acceptsOffers,
            emoji = req.emoji?.trim(),
            coverStyle = req.coverStyle?.trim(),
            status = ShopStatus.PENDING,
        )
        return ShopMapper.toResponse(shopRepository.save(shop))
    }

    /** فروشگاه‌هایِ خودِ کاربر (پنلِ ونـدور). */
    @Transactional(readOnly = true)
    fun mine(userId: Long) =
        shopRepository.findAllByOwnerIdOrderByCreatedAtDesc(userId).map(ShopMapper::toResponse)

    /** نمای عمومیِ فروشگاه (فقط APPROVED برای غیرِمالک). */
    @Transactional(readOnly = true)
    fun getPublic(shopId: Long): ShopResponse {
        val shop = shopRepository.findById(shopId).orElseThrow { ShopNotFoundException(shopId) }
        if (shop.status != ShopStatus.APPROVED) throw ShopNotFoundException(shopId)
        return ShopMapper.toResponse(shop)
    }

    /** فهرستِ فروشگاه‌هایِ تأییدشدهٔ یک محل (rastehSearch) — با فیلترِ اختیاریِ راسته. */
    @Transactional(readOnly = true)
    fun listByLocation(locationId: Long, rastehId: Long?): List<ShopResponse> {
        val shops = if (rastehId != null) {
            shopRepository.findAllByLocationIdAndRastehIdAndStatusOrderByRatingDesc(locationId, rastehId, ShopStatus.APPROVED)
        } else {
            shopRepository.findAllByLocationIdAndStatusOrderByRatingDesc(locationId, ShopStatus.APPROVED)
        }
        return shops.map(ShopMapper::toResponse)
    }
}
