package com.kazemieh.rasteh.canonical.application

import com.kazemieh.rasteh.canonical.persistence.CanonicalProductRepository
import com.kazemieh.rasteh.canonical.persistence.CanonicalShopLocationRepository
import com.kazemieh.rasteh.canonical.persistence.CanonicalVariantRepository
import com.kazemieh.rasteh.canonical.persistence.LegacyProductCrosswalkRepository
import com.kazemieh.rasteh.canonical.persistence.MerchantOrganizationRepository
import com.kazemieh.rasteh.canonical.persistence.ShopOfferRepository
import com.kazemieh.rasteh.catalog.persistence.ProductRepository
import com.kazemieh.rasteh.catalog.persistence.ProductVariantRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class CanonicalMarketplaceBackfillServiceTest {
    @Test
    fun `backfill refuses to write until its feature flag is explicitly enabled`() {
        val service = CanonicalMarketplaceBackfillService(
            dryRunService = mockk<CanonicalBackfillDryRunService>(relaxed = true),
            shopRepository = mockk<ShopRepository>(relaxed = true),
            shopProductRepository = mockk<ShopProductRepository>(relaxed = true),
            catalogProductRepository = mockk<ProductRepository>(relaxed = true),
            catalogVariantRepository = mockk<ProductVariantRepository>(relaxed = true),
            crosswalkRepository = mockk<LegacyProductCrosswalkRepository>(relaxed = true),
            organizationRepository = mockk<MerchantOrganizationRepository>(relaxed = true),
            shopLocationRepository = mockk<CanonicalShopLocationRepository>(relaxed = true),
            canonicalProductRepository = mockk<CanonicalProductRepository>(relaxed = true),
            canonicalVariantRepository = mockk<CanonicalVariantRepository>(relaxed = true),
            shopOfferRepository = mockk<ShopOfferRepository>(relaxed = true),
            writesEnabled = false,
        )

        assertThatThrownBy { service.backfill() }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Canonical marketplace writes are disabled")
    }
}
