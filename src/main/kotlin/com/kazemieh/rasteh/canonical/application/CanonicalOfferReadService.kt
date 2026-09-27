package com.kazemieh.rasteh.canonical.application

import com.kazemieh.rasteh.canonical.persistence.CanonicalProductRepository
import com.kazemieh.rasteh.canonical.persistence.ShopOfferRepository
import com.kazemieh.rasteh.canonical.domain.LegacyIrrMoney
import com.kazemieh.rasteh.canonical.domain.SaleMode
import com.kazemieh.rasteh.marketplace.domain.ShopType
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.UUID

data class CanonicalPageResponse<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

@Service
class CanonicalOfferReadService(
    private val shopOfferRepository: ShopOfferRepository,
    private val productRepository: CanonicalProductRepository,
    private val legacyShopProductRepository: ShopProductRepository,
    @Value("\${app.canonical-marketplace.reads-enabled:false}") private val canonicalReadsEnabled: Boolean,
) {
    fun list(page: Int, size: Int): CanonicalPageResponse<CanonicalOfferResponse> {
        require(page >= 0) { "page must not be negative" }
        require(size in 1..100) { "size must be between 1 and 100" }
        if (!canonicalReadsEnabled) return listLegacy(page, size)
        val offers = shopOfferRepository.findAllByActiveTrue(PageRequest.of(page, size))
        return CanonicalPageResponse(
            items = offers.content.map(::toResponse),
            page = offers.number,
            size = offers.size,
            totalElements = offers.totalElements,
            totalPages = offers.totalPages,
        )
    }

    fun get(id: String): CanonicalOfferResponse {
        if (!canonicalReadsEnabled) {
            val legacyId = id.removePrefix("offer:legacy:").toLongOrNull()
                ?: throw NoSuchElementException("Canonical offer not found")
            val legacy = legacyShopProductRepository.findById(legacyId).filter { it.active }
                .orElseThrow { NoSuchElementException("Canonical offer not found") }
            return toLegacyResponse(legacy)
        }
        val uuid = id.removePrefix("offer:").let(UUID::fromString)
        return shopOfferRepository.findById(uuid).filter { it.active }.map(::toResponse)
            .orElseThrow { NoSuchElementException("Canonical offer not found") }
    }

    private fun listLegacy(page: Int, size: Int): CanonicalPageResponse<CanonicalOfferResponse> {
        val offers = legacyShopProductRepository.findAllByActiveTrueOrderByIdAsc(PageRequest.of(page, size))
        return CanonicalPageResponse(
            items = offers.content.map(::toLegacyResponse),
            page = offers.number,
            size = offers.size,
            totalElements = offers.totalElements,
            totalPages = offers.totalPages,
        )
    }

    private fun toLegacyResponse(legacy: com.kazemieh.rasteh.marketplace.persistence.entity.ShopProductEntity): CanonicalOfferResponse {
        val shop = legacy.shop ?: throw IllegalStateException("Legacy shop product has no shop")
        return CanonicalOfferMapper.fromLegacy(
            legacyShopProductId = legacy.id,
            legacyShopId = shop.id,
            title = legacy.name,
            priceMinorUnits = LegacyIrrMoney.toMinorUnits(legacy.price),
            saleMode = if (shop.type == ShopType.BUYABLE) SaleMode.BUYABLE else SaleMode.SHOWCASE,
            availableQuantity = legacy.stock,
        )
    }

    private fun toResponse(offer: com.kazemieh.rasteh.canonical.persistence.entity.ShopOfferEntity): CanonicalOfferResponse {
        val product = productRepository.findById(offer.productId)
            .orElseThrow { IllegalStateException("Canonical offer references a missing product") }
        return CanonicalOfferMapper.fromCanonical(
            CanonicalOfferView(
                offerId = offer.id,
                productId = product.id,
                shopLocationId = offer.shopLocationId,
                title = product.title,
                priceMinorUnits = offer.priceMinorUnits,
                saleMode = offer.saleMode,
                onHand = offer.onHand,
                reserved = offer.reserved,
            ),
        )
    }
}
