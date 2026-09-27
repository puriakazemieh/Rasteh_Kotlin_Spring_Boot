package com.kazemieh.rasteh.canonical.application

import com.kazemieh.rasteh.canonical.domain.SaleMode
import java.util.UUID

data class CanonicalMoney(
    val minorUnits: Long,
    val currency: String = "IRR",
)

data class CanonicalOfferView(
    val offerId: UUID,
    val productId: UUID,
    val shopLocationId: UUID,
    val title: String,
    val priceMinorUnits: Long,
    val saleMode: SaleMode,
    val onHand: Int,
    val reserved: Int,
)

data class CanonicalOfferResponse(
    val id: String,
    val productId: String,
    val shopLocationId: String,
    val title: String,
    val price: CanonicalMoney,
    val saleMode: SaleMode,
    val availableQuantity: Int,
)

object CanonicalOfferMapper {
    fun fromCanonical(view: CanonicalOfferView): CanonicalOfferResponse =
        CanonicalOfferResponse(
            id = "offer:${view.offerId}",
            productId = "product:${view.productId}",
            shopLocationId = "shop-location:${view.shopLocationId}",
            title = view.title,
            price = CanonicalMoney(view.priceMinorUnits),
            saleMode = view.saleMode,
            availableQuantity = (view.onHand - view.reserved).coerceAtLeast(0),
        )

    fun fromLegacy(
        legacyShopProductId: Long,
        legacyShopId: Long,
        title: String,
        priceMinorUnits: Long,
        saleMode: SaleMode,
        availableQuantity: Int,
    ): CanonicalOfferResponse = CanonicalOfferResponse(
        id = "offer:legacy:$legacyShopProductId",
        productId = "product:legacy-shop-product:$legacyShopProductId",
        shopLocationId = "shop-location:legacy-shop:$legacyShopId",
        title = title,
        price = CanonicalMoney(priceMinorUnits),
        saleMode = saleMode,
        availableQuantity = availableQuantity.coerceAtLeast(0),
    )
}
