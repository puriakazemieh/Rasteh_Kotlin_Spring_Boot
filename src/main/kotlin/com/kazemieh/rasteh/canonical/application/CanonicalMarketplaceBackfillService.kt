package com.kazemieh.rasteh.canonical.application

import com.kazemieh.rasteh.canonical.domain.CanonicalLegacyIds
import com.kazemieh.rasteh.canonical.domain.LegacyIrrMoney
import com.kazemieh.rasteh.canonical.domain.SaleMode
import com.kazemieh.rasteh.canonical.persistence.CanonicalProductRepository
import com.kazemieh.rasteh.canonical.persistence.CanonicalShopLocationRepository
import com.kazemieh.rasteh.canonical.persistence.CanonicalVariantRepository
import com.kazemieh.rasteh.canonical.persistence.LegacyProductCrosswalkRepository
import com.kazemieh.rasteh.canonical.persistence.MerchantOrganizationRepository
import com.kazemieh.rasteh.canonical.persistence.ShopOfferRepository
import com.kazemieh.rasteh.canonical.persistence.entity.CanonicalProductEntity
import com.kazemieh.rasteh.canonical.persistence.entity.CanonicalVariantEntity
import com.kazemieh.rasteh.canonical.persistence.entity.LegacyCrosswalkStatus
import com.kazemieh.rasteh.canonical.persistence.entity.MerchantOrganizationEntity
import com.kazemieh.rasteh.canonical.persistence.entity.ShopLocationEntity
import com.kazemieh.rasteh.canonical.persistence.entity.ShopOfferEntity
import com.kazemieh.rasteh.catalog.persistence.ProductRepository
import com.kazemieh.rasteh.catalog.persistence.ProductVariantRepository
import com.kazemieh.rasteh.marketplace.domain.ShopType
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Opt-in, repeatable expansion backfill. It never deletes or mutates a legacy
 * record. Approved crosswalk rows are the sole source of catalog merges.
 */
@Service
class CanonicalMarketplaceBackfillService(
    private val dryRunService: CanonicalBackfillDryRunService,
    private val shopRepository: ShopRepository,
    private val shopProductRepository: ShopProductRepository,
    private val catalogProductRepository: ProductRepository,
    private val catalogVariantRepository: ProductVariantRepository,
    private val crosswalkRepository: LegacyProductCrosswalkRepository,
    private val organizationRepository: MerchantOrganizationRepository,
    private val shopLocationRepository: CanonicalShopLocationRepository,
    private val canonicalProductRepository: CanonicalProductRepository,
    private val canonicalVariantRepository: CanonicalVariantRepository,
    private val shopOfferRepository: ShopOfferRepository,
    @Value("\${app.canonical-marketplace.writes-enabled:false}") private val writesEnabled: Boolean,
) {
    @Transactional
    fun backfill(): CanonicalBackfillResult {
        check(writesEnabled) { "Canonical marketplace writes are disabled" }
        val report = dryRunService.report()
        check(report.safeToBackfill) { "Dry-run reports invalid legacy marketplace money or stock" }

        val catalogProducts = catalogProductRepository.findAll().associateBy { it.id }
        val catalogVariants = catalogVariantRepository.findAll().associateBy { it.id }
        val approvedCrosswalks = crosswalkRepository.findAllByStatus(LegacyCrosswalkStatus.APPROVED)
            .associateBy { it.shopProductId }
        val shops = shopRepository.findAll().associateBy { it.id }

        require(approvedCrosswalks.values.all { mapping ->
            catalogProducts.containsKey(mapping.catalogProductId) &&
                (mapping.catalogVariantId == null || catalogVariants[mapping.catalogVariantId]?.product?.id == mapping.catalogProductId)
        }) { "Approved crosswalk references a missing or incompatible catalog product/variant" }

        catalogProducts.values.forEach { product ->
            val canonicalId = CanonicalLegacyIds.canonicalProductForCatalogProduct(product.id)
            canonicalProductRepository.findById(canonicalId).orElseGet {
                canonicalProductRepository.save(
                    CanonicalProductEntity(
                        id = canonicalId,
                        externalCatalogProductId = product.id,
                        title = product.title,
                        slug = product.slug,
                        description = product.description,
                    ),
                )
            }
        }
        catalogVariants.values.forEach { variant ->
            val productId = variant.product?.id ?: error("Catalog variant ${variant.id} has no product")
            val canonicalId = CanonicalLegacyIds.canonicalVariantForCatalogVariant(variant.id)
            canonicalVariantRepository.findById(canonicalId).orElseGet {
                canonicalVariantRepository.save(
                    CanonicalVariantEntity(
                        id = canonicalId,
                        productId = CanonicalLegacyIds.canonicalProductForCatalogProduct(productId),
                        externalCatalogVariantId = variant.id,
                        sku = variant.sku,
                        title = variant.sku,
                    ),
                )
            }
        }
        shops.values.forEach { shop ->
            val ownerId = shop.owner?.id ?: error("Shop ${shop.id} has no owner")
            val organizationId = CanonicalLegacyIds.merchantOrganization(shop.id)
            organizationRepository.findById(organizationId).orElseGet {
                organizationRepository.save(
                    MerchantOrganizationEntity(
                        id = organizationId,
                        externalShopId = shop.id,
                        ownerUserId = ownerId,
                        displayName = shop.name,
                    ),
                )
            }
            val locationId = CanonicalLegacyIds.shopLocation(shop.id)
            shopLocationRepository.findById(locationId).orElseGet {
                shopLocationRepository.save(
                    ShopLocationEntity(
                        id = locationId,
                        merchantOrganizationId = organizationId,
                        externalShopId = shop.id,
                        externalLocationId = shop.location?.id,
                        name = shop.name,
                        address = shop.address,
                    ),
                )
            }
        }

        shopProductRepository.findAll().forEach { legacyProduct ->
            val shop = legacyProduct.shop ?: error("Shop product ${legacyProduct.id} has no shop")
            require(shops.containsKey(shop.id)) { "Shop product ${legacyProduct.id} references a missing shop" }
            val mapping = approvedCrosswalks[legacyProduct.id]
            val canonicalProductId = mapping?.let {
                CanonicalLegacyIds.canonicalProductForCatalogProduct(it.catalogProductId)
            } ?: CanonicalLegacyIds.canonicalProductForShopProduct(legacyProduct.id)
            if (mapping == null) {
                canonicalProductRepository.findById(canonicalProductId).orElseGet {
                    canonicalProductRepository.save(
                        CanonicalProductEntity(
                            id = canonicalProductId,
                            externalShopProductId = legacyProduct.id,
                            title = legacyProduct.name,
                            description = legacyProduct.description,
                        ),
                    )
                }
            }
            val canonicalVariantId = mapping?.catalogVariantId?.let(CanonicalLegacyIds::canonicalVariantForCatalogVariant)
                ?: CanonicalLegacyIds.canonicalVariantForShopProduct(legacyProduct.id)
            if (mapping?.catalogVariantId == null) {
                canonicalVariantRepository.findById(canonicalVariantId).orElseGet {
                    canonicalVariantRepository.save(
                        CanonicalVariantEntity(
                            id = canonicalVariantId,
                            productId = canonicalProductId,
                            sku = "legacy-shop-product:${legacyProduct.id}",
                            title = legacyProduct.name,
                        ),
                    )
                }
            }
            val offerId = CanonicalLegacyIds.offerForShopProduct(legacyProduct.id)
            if (!shopOfferRepository.existsById(offerId)) {
                shopOfferRepository.save(
                    ShopOfferEntity(
                        id = offerId,
                        shopLocationId = CanonicalLegacyIds.shopLocation(shop.id),
                        productId = canonicalProductId,
                        variantId = canonicalVariantId,
                        externalShopProductId = legacyProduct.id,
                        priceMinorUnits = LegacyIrrMoney.toMinorUnits(legacyProduct.price),
                        saleMode = if (shop.type == ShopType.BUYABLE) SaleMode.BUYABLE else SaleMode.SHOWCASE,
                        onHand = legacyProduct.stock,
                        active = legacyProduct.active,
                    ),
                )
            }
        }

        return CanonicalBackfillResult(
            canonicalCatalogProductCount = catalogProducts.size,
            canonicalCatalogVariantCount = catalogVariants.size,
            canonicalShopCount = shops.size,
            canonicalOfferCount = shopOfferRepository.count(),
            approvedCrosswalkCount = approvedCrosswalks.size,
        )
    }
}

data class CanonicalBackfillResult(
    val canonicalCatalogProductCount: Int,
    val canonicalCatalogVariantCount: Int,
    val canonicalShopCount: Int,
    val canonicalOfferCount: Long,
    val approvedCrosswalkCount: Int,
)
