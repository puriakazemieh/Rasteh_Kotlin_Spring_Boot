package com.kazemieh.rasteh.catalog.application

import com.kazemieh.rasteh.catalog.api.dto.PageResponse
import com.kazemieh.rasteh.catalog.api.dto.ProductSummaryResponse
import com.kazemieh.rasteh.catalog.application.exception.ProductNotFoundException
import com.kazemieh.rasteh.catalog.persistence.FavoriteRepository
import com.kazemieh.rasteh.catalog.persistence.ProductImageRepository
import com.kazemieh.rasteh.catalog.persistence.ProductRepository
import com.kazemieh.rasteh.catalog.persistence.ProductReviewRepository
import com.kazemieh.rasteh.catalog.persistence.ProductVariantRepository
import com.kazemieh.rasteh.catalog.persistence.RecentlyViewedRepository
import com.kazemieh.rasteh.catalog.persistence.entity.RecentlyViewedEntity
import com.kazemieh.rasteh.identity.application.exception.UserNotFoundException
import com.kazemieh.rasteh.identity.persistence.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class RecentlyViewedService(
    private val recentlyViewedRepository: RecentlyViewedRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val productImageRepository: ProductImageRepository,
    private val variantRepository: ProductVariantRepository,
    private val productReviewRepository: ProductReviewRepository,
    private val favoriteRepository: FavoriteRepository,
) {

    @Transactional
    fun recordView(userId: Long, productId: Long) {
        val existing = recentlyViewedRepository.findByUserIdAndProductId(userId, productId)
        if (existing != null) {
            existing.viewedAt = OffsetDateTime.now()
            recentlyViewedRepository.save(existing)
            return
        }

        val user = userRepository.findById(userId).orElseThrow { UserNotFoundException() }
        val product = productRepository.findById(productId).orElseThrow { ProductNotFoundException(productId.toString()) }

        recentlyViewedRepository.save(RecentlyViewedEntity(user = user, product = product))
    }

    @Transactional(readOnly = true)
    fun getRecentlyViewed(userId: Long, page: Int, size: Int): PageResponse<ProductSummaryResponse> {
        val pageable = PageRequest.of(page, size)
        val recentPage = recentlyViewedRepository.findAllByUserIdOrderByViewedAtDesc(userId, pageable)

        val products = recentPage.content.map { it.product }
        val productIds = products.map { it.id }

        val images = if (productIds.isNotEmpty())
            productImageRepository.findAllByProductIdInOrderBySortOrderAsc(productIds)
        else emptyList()

        val thumbnailByProductId = images
            .groupBy { it.product?.id ?: 0L }
            .mapValues { (_, list) -> list.firstOrNull()?.url }

        val aggByProductId = if (productIds.isNotEmpty())
            variantRepository.aggregateByProductIds(productIds).associateBy { it.getProductId() }
        else emptyMap()

        val ratingByProductId = if (productIds.isNotEmpty())
            productReviewRepository.aggregateRatingsByProductIds(productIds).associateBy { it.getProductId() }
        else emptyMap()

        val favoriteIds = if (productIds.isNotEmpty())
            favoriteRepository.findAllByUserIdAndProductIdIn(userId, productIds).map { it.product.id }.toSet()
        else emptySet()

        val items = products.map { p ->
            val agg = aggByProductId[p.id]
            val rating = ratingByProductId[p.id]
            ProductSummaryResponse(
                id = p.id,
                title = p.title,
                slug = p.slug,
                thumbnailUrl = thumbnailByProductId[p.id],
                minPrice = agg?.getMinPrice(),
                maxPrice = agg?.getMaxPrice(),
                minDiscountedPrice = agg?.getMinDiscountedPrice(),
                maxDiscountedPrice = agg?.getMaxDiscountedPrice(),
                inStock = agg?.getInStock() ?: false,
                categoryId = p.category?.id,
                categoryName = p.category?.name,
                isFavorite = favoriteIds.contains(p.id),
                averageRating = rating?.getAvgRating(),
                reviewCount = rating?.getReviewCount() ?: 0,
            )
        }

        return PageResponse(
            items = items,
            page = recentPage.number,
            size = recentPage.size,
            totalElements = recentPage.totalElements,
            totalPages = recentPage.totalPages
        )
    }
}
