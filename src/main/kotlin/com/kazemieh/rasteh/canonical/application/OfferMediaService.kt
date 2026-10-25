package com.kazemieh.rasteh.canonical.application

import com.kazemieh.rasteh.canonical.persistence.CanonicalShopLocationRepository
import com.kazemieh.rasteh.canonical.persistence.OfferMediaRepository
import com.kazemieh.rasteh.canonical.persistence.ShopOfferRepository
import com.kazemieh.rasteh.canonical.persistence.entity.OfferMediaEntity
import com.kazemieh.rasteh.identity.authorization.AuthorizationService
import com.kazemieh.rasteh.identity.authorization.Capability
import com.kazemieh.rasteh.shared.error.BadRequestException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/** Storage stays private: this contract records keys only, never public object URLs. */
interface OfferMediaStorage { fun createPrivateUpload(objectKey: String, contentType: String): PrivateUpload }
data class PrivateUpload(val objectKey: String, val uploadUrl: String)

@Service
class OfferMediaService(
    private val offers: ShopOfferRepository,
    private val locations: CanonicalShopLocationRepository,
    private val media: OfferMediaRepository,
    private val auth: AuthorizationService,
) {
    @Transactional
    fun attach(actorId: Long, offerId: UUID, objectKey: String, mediaType: String, position: Int): OfferMediaEntity {
        require(objectKey.matches(Regex("[A-Za-z0-9/_\\-.]{1,255}"))) { "Invalid private media object key" }
        require(mediaType.startsWith("image/")) { "Only image media is accepted" }
        val offer = offers.findById(offerId).orElseThrow { BadRequestException("Offer not found", "OFFER_NOT_FOUND") }
        val location = locations.findById(offer.shopLocationId).orElseThrow { BadRequestException("Shop location not found", "SHOP_LOCATION_NOT_FOUND") }
        auth.require(actorId, Capability.SHOP_CATALOG_WRITE, location.externalShopId)
        return media.save(OfferMediaEntity(offerId = offerId, objectKey = objectKey, mediaType = mediaType, position = position.coerceAtLeast(0)))
    }
    fun list(offerId: UUID): List<OfferMediaEntity> = media.findAllByOfferIdOrderByPositionAsc(offerId)
}
