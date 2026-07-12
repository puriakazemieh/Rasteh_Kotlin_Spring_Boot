package com.kazemieh.rasteh.marketplace.application

import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.marketplace.api.dto.CreateReviewRequest
import com.kazemieh.rasteh.marketplace.api.dto.ReviewResponse
import com.kazemieh.rasteh.marketplace.api.mapper.ReviewMapper
import com.kazemieh.rasteh.marketplace.persistence.ReviewRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.marketplace.persistence.entity.ReviewEntity
import com.kazemieh.rasteh.shared.error.ShopNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val shopRepository: ShopRepository,
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = true)
    fun listByShop(shopId: Long): List<ReviewResponse> =
        reviewRepository.findAllByShopIdOrderByIdDesc(shopId).map(ReviewMapper::toResponse)

    /** ثبت/به‌روزرسانیِ نظر (یک نظر به‌ازای هر کاربر) + بازمحاسبهٔ میانگینِ فروشگاه. */
    @Transactional
    fun create(userId: Long, req: CreateReviewRequest): ReviewResponse {
        val shop = shopRepository.findById(req.shopId).orElseThrow { ShopNotFoundException(req.shopId) }
        val author = userRepository.findById(userId).orElse(null)
        val authorName = author?.let {
            listOfNotNull(it.firstName?.trim(), it.lastName?.trim()).joinToString(" ").ifBlank { null }
        }

        val review = reviewRepository.findByUserIdAndShopId(userId, req.shopId)?.also {
            it.rating = req.rating
            it.comment = req.comment?.trim()
            it.authorName = authorName
        } ?: reviewRepository.save(
            ReviewEntity(
                shop = shop,
                userId = userId,
                authorName = authorName,
                rating = req.rating,
                comment = req.comment?.trim(),
            )
        )

        // بازمحاسبهٔ میانگین و شمارش.
        val all = reviewRepository.findAllByShopIdOrderByIdDesc(req.shopId)
        val count = all.size
        val avg = if (count == 0) BigDecimal.ZERO
        else BigDecimal(all.sumOf { it.rating }).divide(BigDecimal(count), 2, RoundingMode.HALF_UP)
        shop.reviewsCount = count
        shop.rating = avg

        return ReviewMapper.toResponse(review)
    }
}
