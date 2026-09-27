package com.kazemieh.rasteh.canonical.application

import com.kazemieh.rasteh.canonical.persistence.CanonicalProductRepository
import com.kazemieh.rasteh.canonical.persistence.ShopOfferRepository
import com.kazemieh.rasteh.marketplace.domain.ShopType
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopEntity
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopProductEntity
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class CanonicalOfferReadServiceTest {
    @Test
    fun `legacy read adapter exposes exact IRR v1 offer while canonical reads are disabled`() {
        val shopProducts = mockk<ShopProductRepository>()
        every { shopProducts.findAllByActiveTrueOrderByIdAsc(any<Pageable>()) } returns PageImpl(
            listOf(
                ShopProductEntity(
                    id = 9,
                    shop = ShopEntity(id = 4, type = ShopType.BUYABLE),
                    name = "Local product",
                    price = BigDecimal("250000"),
                    stock = 2,
                ),
            ),
        )

        val response = CanonicalOfferReadService(
            mockk<ShopOfferRepository>(relaxed = true),
            mockk<CanonicalProductRepository>(relaxed = true),
            shopProducts,
            false,
        ).list(0, 20)

        assertThat(response.items.single().id).isEqualTo("offer:legacy:9")
        assertThat(response.items.single().price).isEqualTo(CanonicalMoney(250000L, "IRR"))
    }
}
