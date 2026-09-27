package com.kazemieh.rasteh.canonical.persistence

import com.kazemieh.rasteh.canonical.persistence.entity.CanonicalProductEntity
import com.kazemieh.rasteh.canonical.persistence.entity.CanonicalVariantEntity
import com.kazemieh.rasteh.canonical.persistence.entity.LegacyCrosswalkStatus
import com.kazemieh.rasteh.canonical.persistence.entity.LegacyProductCrosswalkEntity
import com.kazemieh.rasteh.canonical.persistence.entity.MerchantOrganizationEntity
import com.kazemieh.rasteh.canonical.persistence.entity.ShopLocationEntity
import com.kazemieh.rasteh.canonical.persistence.entity.ShopOfferEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
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
}
