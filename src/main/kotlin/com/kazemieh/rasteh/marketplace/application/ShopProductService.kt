package com.kazemieh.rasteh.marketplace.application

import com.kazemieh.rasteh.marketplace.api.dto.CreateProductRequest
import com.kazemieh.rasteh.marketplace.api.dto.ProductResponse
import com.kazemieh.rasteh.marketplace.api.dto.UpdateProductRequest
import com.kazemieh.rasteh.marketplace.api.mapper.ProductMapper
import com.kazemieh.rasteh.marketplace.domain.ShopStatus
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopEntity
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopProductEntity
import com.kazemieh.rasteh.shared.error.ProductNotFoundException
import com.kazemieh.rasteh.shared.error.ShopAccessDeniedException
import com.kazemieh.rasteh.shared.error.ShopNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ShopProductService(
    private val productRepository: ShopProductRepository,
    private val shopRepository: ShopRepository,
) {

    // ===== عمومی (خواننده) =====

    /** کالاهایِ فعالِ یک فروشگاهِ تأییدشده. */
    @Transactional(readOnly = true)
    fun listPublicByShop(shopId: Long): List<ProductResponse> {
        val shop = shopRepository.findById(shopId).orElseThrow { ShopNotFoundException(shopId) }
        if (shop.status != ShopStatus.APPROVED) throw ShopNotFoundException(shopId)
        return productRepository.findAllByShopIdAndActiveTrueOrderByIdDesc(shopId).map(ProductMapper::toResponse)
    }

    /** «دیدن در فروشگاه‌های دیگر» — کالاهایِ هم‌نام در فروشگاه‌های تأییدشدهٔ دیگر (ارزان‌ترین اول). */
    @Transactional(readOnly = true)
    fun otherSellers(productId: Long): List<ProductResponse> {
        val product = productRepository.findById(productId).orElseThrow { ProductNotFoundException() }
        val shopId = product.shop?.id ?: return emptyList()
        return productRepository.findOtherSellers(product.name, shopId, ShopStatus.APPROVED).map(ProductMapper::toResponse)
    }

    /** جزئیاتِ یک کالا (فقط اگر فعال و فروشگاه تأییدشده باشد). */
    @Transactional(readOnly = true)
    fun getPublic(productId: Long): ProductResponse {
        val product = productRepository.findById(productId).orElseThrow { ProductNotFoundException() }
        val shop = product.shop
        if (!product.active || shop == null || shop.status != ShopStatus.APPROVED) {
            throw ProductNotFoundException()
        }
        return ProductMapper.toResponse(product)
    }

    // ===== ونـدور (نویسنده) =====

    /** همهٔ کالاهایِ فروشگاهِ خودِ ونـدور (شاملِ غیرفعال) — پنلِ مدیریتِ کالاها. */
    @Transactional(readOnly = true)
    fun listVendorByShop(userId: Long, isAdmin: Boolean, shopId: Long): List<ProductResponse> {
        requireOwnership(shopRepository.findById(shopId).orElseThrow { ShopNotFoundException(shopId) }, userId, isAdmin)
        return productRepository.findAllByShopIdOrderByIdDesc(shopId).map(ProductMapper::toResponse)
    }

    @Transactional
    fun create(userId: Long, isAdmin: Boolean, req: CreateProductRequest): ProductResponse {
        val shop = shopRepository.findById(req.shopId).orElseThrow { ShopNotFoundException(req.shopId) }
        requireOwnership(shop, userId, isAdmin)
        val product = ShopProductEntity(
            shop = shop,
            name = req.name.trim(),
            description = req.description?.trim(),
            price = req.price,
            oldPrice = req.oldPrice,
            discountPercent = req.discountPercent,
            condition = req.condition,
            stock = req.stock,
            categoryName = req.categoryName?.trim(),
            emoji = req.emoji?.trim(),
            imageUrl = req.imageUrl?.trim(),
            active = true,
        )
        return ProductMapper.toResponse(productRepository.save(product))
    }

    @Transactional
    fun update(userId: Long, isAdmin: Boolean, productId: Long, req: UpdateProductRequest): ProductResponse {
        val product = productRepository.findById(productId).orElseThrow { ProductNotFoundException() }
        requireOwnership(product.shop ?: throw ProductNotFoundException(), userId, isAdmin)
        req.name?.let { product.name = it.trim() }
        req.description?.let { product.description = it.trim() }
        req.price?.let { product.price = it }
        req.oldPrice?.let { product.oldPrice = it }
        req.discountPercent?.let { product.discountPercent = it }
        req.condition?.let { product.condition = it }
        req.stock?.let { product.stock = it }
        req.categoryName?.let { product.categoryName = it.trim() }
        req.emoji?.let { product.emoji = it.trim() }
        req.imageUrl?.let { product.imageUrl = it.trim() }
        req.active?.let { product.active = it }
        return ProductMapper.toResponse(product)
    }

    @Transactional
    fun delete(userId: Long, isAdmin: Boolean, productId: Long) {
        val product = productRepository.findById(productId).orElseThrow { ProductNotFoundException() }
        requireOwnership(product.shop ?: throw ProductNotFoundException(), userId, isAdmin)
        productRepository.delete(product)
    }

    private fun requireOwnership(shop: ShopEntity, userId: Long, isAdmin: Boolean) {
        if (isAdmin) return
        if (shop.owner?.id != userId) throw ShopAccessDeniedException(shop.id)
    }
}
