package com.kazemieh.rasteh.marketplace.api.mapper

import com.kazemieh.rasteh.marketplace.api.dto.ReviewResponse
import com.kazemieh.rasteh.marketplace.persistence.entity.ReviewEntity

object ReviewMapper {
    fun toResponse(r: ReviewEntity) = ReviewResponse(
        id = r.id,
        shopId = r.shop?.id,
        userId = r.userId,
        authorName = r.authorName,
        rating = r.rating,
        comment = r.comment,
        createdAt = r.createdAt,
    )
}
