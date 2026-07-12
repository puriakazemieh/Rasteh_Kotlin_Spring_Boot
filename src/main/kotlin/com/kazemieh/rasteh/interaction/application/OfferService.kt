package com.kazemieh.rasteh.interaction.application

import com.kazemieh.rasteh.identity.application.exception.UserNotFoundException
import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.interaction.api.dto.CreateOfferRequest
import com.kazemieh.rasteh.interaction.api.dto.OfferResponse
import com.kazemieh.rasteh.interaction.api.mapper.OfferMapper
import com.kazemieh.rasteh.interaction.domain.OfferStatus
import com.kazemieh.rasteh.interaction.persistence.OfferRepository
import com.kazemieh.rasteh.interaction.persistence.entity.OfferEntity
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.shared.error.OfferAccessDeniedException
import com.kazemieh.rasteh.shared.error.OfferNotFoundException
import com.kazemieh.rasteh.shared.error.ShopDoesNotAcceptOffersException
import com.kazemieh.rasteh.shared.error.ShopNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OfferService(
    private val offerRepository: OfferRepository,
    private val shopRepository: ShopRepository,
    private val productRepository: ShopProductRepository,
    private val userRepository: UserRepository,
) {

    /** ثبتِ پیشنهادِ قیمت توسطِ خریدار — فقط اگر فروشگاه پیشنهاد بپذیرد. */
    @Transactional
    fun create(userId: Long, req: CreateOfferRequest): OfferResponse {
        val shop = shopRepository.findById(req.shopId).orElseThrow { ShopNotFoundException(req.shopId) }
        if (!shop.acceptsOffers) throw ShopDoesNotAcceptOffersException(req.shopId)
        val customer = userRepository.findById(userId).orElseThrow { UserNotFoundException() }
        val product = req.productId?.let { productRepository.findById(it).orElse(null) }
        val offer = offerRepository.save(
            OfferEntity(
                shop = shop,
                product = product,
                customer = customer,
                amount = req.amount,
                message = req.message?.trim(),
                status = OfferStatus.PENDING,
            )
        )
        return OfferMapper.toResponse(offer)
    }

    /** پیشنهادهایِ خودِ خریدار. */
    @Transactional(readOnly = true)
    fun listMine(userId: Long): List<OfferResponse> =
        offerRepository.findAllByCustomerIdOrderByIdDesc(userId).map(OfferMapper::toResponse)

    /** پیشنهادهایِ یک فروشگاه (برایِ فروشنده/ادمین). */
    @Transactional(readOnly = true)
    fun listForShop(userId: Long, isAdmin: Boolean, shopId: Long): List<OfferResponse> {
        val shop = shopRepository.findById(shopId).orElseThrow { ShopNotFoundException(shopId) }
        if (!isAdmin && shop.owner?.id != userId) throw OfferAccessDeniedException(shopId)
        return offerRepository.findAllByShopIdOrderByIdDesc(shopId).map(OfferMapper::toResponse)
    }

    @Transactional
    fun accept(userId: Long, isAdmin: Boolean, offerId: Long) = setStatus(userId, isAdmin, offerId, OfferStatus.ACCEPTED)

    @Transactional
    fun reject(userId: Long, isAdmin: Boolean, offerId: Long) = setStatus(userId, isAdmin, offerId, OfferStatus.REJECTED)

    private fun setStatus(userId: Long, isAdmin: Boolean, offerId: Long, status: OfferStatus): OfferResponse {
        val offer = offerRepository.findById(offerId).orElseThrow { OfferNotFoundException(offerId) }
        if (!isAdmin && offer.shop?.owner?.id != userId) throw OfferAccessDeniedException(offerId)
        offer.status = status
        return OfferMapper.toResponse(offer)
    }
}
