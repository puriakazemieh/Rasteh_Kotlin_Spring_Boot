package com.kazemieh.rasteh.canonical.application

import com.kazemieh.rasteh.canonical.domain.LegacyIrrMoney
import com.kazemieh.rasteh.canonical.persistence.ShopOfferRepository
import com.kazemieh.rasteh.catalog.persistence.ProductRepository
import com.kazemieh.rasteh.catalog.persistence.ProductVariantRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.marketplace.order.MarketplaceOrderRepository
import org.springframework.stereotype.Service

/**
 * Read-only migration rehearsal. It deliberately has no save/delete dependency:
 * an operator receives the exception list before any canonical backfill is allowed.
 */
@Service
class CanonicalBackfillDryRunService(
    private val shopRepository: ShopRepository,
    private val productRepository: ProductRepository,
    private val variantRepository: ProductVariantRepository,
    private val shopProductRepository: ShopProductRepository,
    private val marketplaceOrderRepository: MarketplaceOrderRepository,
    private val shopOfferRepository: ShopOfferRepository,
) {
    fun report(): CanonicalBackfillDryRunReport {
        val legacyShopProducts = shopProductRepository.findAll()
        val invalidAmountIds = legacyShopProducts.asSequence()
            .filter { product -> runCatching { LegacyIrrMoney.toMinorUnits(product.price) }.isFailure }
            .map { it.id }
            .take(MAX_REPORTED_EXCEPTIONS)
            .toList()
        val invalidStockIds = legacyShopProducts.asSequence()
            .filter { it.stock < 0 }
            .map { it.id }
            .take(MAX_REPORTED_EXCEPTIONS)
            .toList()

        return CanonicalBackfillDryRunReport(
            shopCount = shopRepository.count(),
            catalogProductCount = productRepository.count(),
            catalogVariantCount = variantRepository.count(),
            shopProductCount = legacyShopProducts.size.toLong(),
            marketplaceOrderCount = marketplaceOrderRepository.count(),
            canonicalOfferCount = shopOfferRepository.count(),
            invalidMarketplaceAmountProductIds = invalidAmountIds,
            invalidMarketplaceStockProductIds = invalidStockIds,
            safeToBackfill = invalidAmountIds.isEmpty() && invalidStockIds.isEmpty(),
        )
    }

    companion object {
        private const val MAX_REPORTED_EXCEPTIONS = 100
    }
}

data class CanonicalBackfillDryRunReport(
    val shopCount: Long,
    val catalogProductCount: Long,
    val catalogVariantCount: Long,
    val shopProductCount: Long,
    val marketplaceOrderCount: Long,
    val canonicalOfferCount: Long,
    val invalidMarketplaceAmountProductIds: List<Long>,
    val invalidMarketplaceStockProductIds: List<Long>,
    val safeToBackfill: Boolean,
)
