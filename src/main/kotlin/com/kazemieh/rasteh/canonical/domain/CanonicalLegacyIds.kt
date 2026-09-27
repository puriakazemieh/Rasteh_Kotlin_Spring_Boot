package com.kazemieh.rasteh.canonical.domain

import java.nio.charset.StandardCharsets.UTF_8
import java.util.UUID

/** Stable IDs make a gated backfill safe to retry without duplicate records. */
object CanonicalLegacyIds {
    fun merchantOrganization(shopId: Long): UUID = id("merchant-organization:shop:$shopId")
    fun shopLocation(shopId: Long): UUID = id("shop-location:shop:$shopId")
    fun canonicalProductForCatalogProduct(productId: Long): UUID = id("product:catalog:$productId")
    fun canonicalProductForShopProduct(productId: Long): UUID = id("product:shop-product:$productId")
    fun canonicalVariantForCatalogVariant(variantId: Long): UUID = id("variant:catalog:$variantId")
    fun canonicalVariantForShopProduct(productId: Long): UUID = id("variant:shop-product:$productId")
    fun offerForShopProduct(productId: Long): UUID = id("offer:shop-product:$productId")

    private fun id(value: String): UUID = UUID.nameUUIDFromBytes("rasteh:v1:$value".toByteArray(UTF_8))
}
