package com.kazemieh.rasteh.canonical.application

import com.kazemieh.rasteh.canonical.persistence.ShopOfferRepository
import com.kazemieh.rasteh.catalog.persistence.ProductRepository
import com.kazemieh.rasteh.catalog.persistence.ProductVariantRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopProductEntity
import com.kazemieh.rasteh.marketplace.order.MarketplaceOrderRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CanonicalBackfillDryRunServiceTest {
    private val shopRepository = mockk<ShopRepository>()
    private val productRepository = mockk<ProductRepository>()
    private val variantRepository = mockk<ProductVariantRepository>()
    private val shopProductRepository = mockk<ShopProductRepository>()
    private val marketplaceOrderRepository = mockk<MarketplaceOrderRepository>()
    private val shopOfferRepository = mockk<ShopOfferRepository>()

    @Test
    fun `reports malformed legacy marketplace money and never writes`() {
        every { shopRepository.count() } returns 2L
        every { productRepository.count() } returns 5L
        every { variantRepository.count() } returns 8L
        every { marketplaceOrderRepository.count() } returns 3L
        every { shopOfferRepository.count() } returns 0L
        every { shopProductRepository.findAll() } returns listOf(
            ShopProductEntity(id = 10, price = BigDecimal("100000")),
            ShopProductEntity(id = 11, price = BigDecimal("12.50")),
            ShopProductEntity(id = 12, price = BigDecimal("-1"), stock = -1),
        )

        val report = CanonicalBackfillDryRunService(
            shopRepository,
            productRepository,
            variantRepository,
            shopProductRepository,
            marketplaceOrderRepository,
            shopOfferRepository,
        ).report()

        assertThat(report.safeToBackfill).isFalse()
        assertThat(report.invalidMarketplaceAmountProductIds).containsExactly(11L, 12L)
        assertThat(report.invalidMarketplaceStockProductIds).containsExactly(12L)
        assertThat(report.shopProductCount).isEqualTo(3L)
        assertThat(report.canonicalOfferCount).isZero()
    }
}
