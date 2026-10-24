package com.kazemieh.rasteh.canonical.persistence

import com.kazemieh.rasteh.canonical.persistence.entity.CanonicalProductEntity
import com.kazemieh.rasteh.canonical.persistence.entity.CanonicalVariantEntity
import com.kazemieh.rasteh.canonical.persistence.entity.LegacyCrosswalkStatus
import com.kazemieh.rasteh.canonical.persistence.entity.LegacyProductCrosswalkEntity
import com.kazemieh.rasteh.canonical.persistence.entity.MerchantOrganizationEntity
import com.kazemieh.rasteh.canonical.persistence.entity.ShopLocationEntity
import com.kazemieh.rasteh.canonical.persistence.entity.ShopOfferEntity
import com.kazemieh.rasteh.canonical.persistence.entity.InventoryLedgerEntity
import com.kazemieh.rasteh.canonical.persistence.entity.OfferReservationEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import jakarta.persistence.LockModeType
import java.util.UUID

interface CanonicalProductRepository : JpaRepository<CanonicalProductEntity, UUID>

interface CanonicalVariantRepository : JpaRepository<CanonicalVariantEntity, UUID>

interface MerchantOrganizationRepository : JpaRepository<MerchantOrganizationEntity, UUID>

interface CanonicalShopLocationRepository : JpaRepository<ShopLocationEntity, UUID>

interface LegacyProductCrosswalkRepository : JpaRepository<LegacyProductCrosswalkEntity, Long> {
    fun findAllByStatus(status: LegacyCrosswalkStatus): List<LegacyProductCrosswalkEntity>
}

interface ShopOfferRepository : JpaRepository<ShopOfferEntity, UUID> {
    fun findAllByActiveTrue(pageable: Pageable): Page<ShopOfferEntity>
    @Lock(LockModeType.PESSIMISTIC_WRITE) fun findLockedById(id: UUID): ShopOfferEntity?
}
interface InventoryLedgerRepository : JpaRepository<InventoryLedgerEntity, UUID> { fun findByOfferIdAndIdempotencyKey(offerId: UUID, idempotencyKey: String): InventoryLedgerEntity? }
interface OfferReservationRepository : JpaRepository<OfferReservationEntity, UUID> { fun findByIdempotencyKey(idempotencyKey: String): OfferReservationEntity?; fun findAllByStatusAndExpiresAtBefore(status: String, expiresAt: java.time.OffsetDateTime): List<OfferReservationEntity> }
