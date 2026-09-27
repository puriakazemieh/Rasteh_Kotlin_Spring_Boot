package com.kazemieh.rasteh.canonical.application

import com.kazemieh.rasteh.canonical.domain.SaleMode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class CanonicalOfferMapperTest {
    @Test
    fun `canonical offer response carries an opaque typed id and exact IRR money`() {
        val offerId = UUID.fromString("018f26a8-b24a-7b18-87ff-8b83042df99a")

        val response = CanonicalOfferMapper.fromCanonical(
            CanonicalOfferView(
                offerId = offerId,
                productId = UUID.fromString("018f26a8-b24a-7b18-87ff-8b83042df99b"),
                shopLocationId = UUID.fromString("018f26a8-b24a-7b18-87ff-8b83042df99c"),
                title = "Test product",
                priceMinorUnits = 125000L,
                saleMode = SaleMode.BUYABLE,
                onHand = 3,
                reserved = 1,
            ),
        )

        assertThat(response.id).isEqualTo("offer:$offerId")
        assertThat(response.price).isEqualTo(CanonicalMoney(125000L, "IRR"))
        assertThat(response.availableQuantity).isEqualTo(2)
    }

    @Test
    fun `legacy adapter preserves approved IRR amount without a toman conversion`() {
        val response = CanonicalOfferMapper.fromLegacy(
            legacyShopProductId = 7,
            legacyShopId = 3,
            title = "Legacy product",
            priceMinorUnits = 45000L,
            saleMode = SaleMode.SHOWCASE,
            availableQuantity = 0,
        )

        assertThat(response.id).isEqualTo("offer:legacy:7")
        assertThat(response.price).isEqualTo(CanonicalMoney(45000L, "IRR"))
    }
}
