package com.kazemieh.rasteh.interaction.application

import com.kazemieh.rasteh.interaction.api.dto.BookmarkResponse
import com.kazemieh.rasteh.interaction.api.dto.CreateBookmarkRequest
import com.kazemieh.rasteh.interaction.api.mapper.BookmarkMapper
import com.kazemieh.rasteh.interaction.persistence.BookmarkRepository
import com.kazemieh.rasteh.interaction.persistence.entity.BookmarkEntity
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.shared.error.BadRequestException
import com.kazemieh.rasteh.shared.error.ErrorCodes
import com.kazemieh.rasteh.shared.error.ProductNotFoundException
import com.kazemieh.rasteh.shared.error.ShopNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookmarkService(
    private val bookmarkRepository: BookmarkRepository,
    private val shopRepository: ShopRepository,
    private val productRepository: ShopProductRepository,
) {

    /** افزودنِ نشان (idempotent) — اگر از قبل باشد همان را برمی‌گرداند. */
    @Transactional
    fun add(userId: Long, req: CreateBookmarkRequest): BookmarkResponse {
        val shopId = req.shopId
        val productId = req.productId
        if (shopId == null && productId == null) {
            throw BadRequestException("shopId or productId required", ErrorCodes.INVALID_INPUT)
        }
        if (shopId != null) {
            bookmarkRepository.findByUserIdAndShopId(userId, shopId)?.let { return BookmarkMapper.toResponse(it) }
            val shop = shopRepository.findById(shopId).orElseThrow { ShopNotFoundException(shopId) }
            return BookmarkMapper.toResponse(bookmarkRepository.save(BookmarkEntity(userId = userId, shop = shop)))
        }
        // productId != null
        bookmarkRepository.findByUserIdAndProductId(userId, productId!!)?.let { return BookmarkMapper.toResponse(it) }
        val product = productRepository.findById(productId).orElseThrow { ProductNotFoundException() }
        return BookmarkMapper.toResponse(bookmarkRepository.save(BookmarkEntity(userId = userId, product = product)))
    }

    /** حذفِ نشان بر اساسِ شناسه (فقط نشانِ خودِ کاربر). */
    @Transactional
    fun remove(userId: Long, bookmarkId: Long) {
        val bookmark = bookmarkRepository.findById(bookmarkId).orElse(null) ?: return
        if (bookmark.userId == userId) bookmarkRepository.delete(bookmark)
    }

    @Transactional(readOnly = true)
    fun listMine(userId: Long): List<BookmarkResponse> =
        bookmarkRepository.findAllByUserIdOrderByIdDesc(userId).map(BookmarkMapper::toResponse)
}
