package com.kazemieh.rasteh.advanced

import jakarta.validation.constraints.NotBlank
import java.time.OffsetDateTime

data class CreatePostRequest(@field:NotBlank val body: String)
data class CreateCommentRequest(@field:NotBlank val body: String)
data class PostResponse(val id: Long, val userId: Long, val authorName: String?, val body: String, val commentCount: Int, val createdAt: OffsetDateTime?)
data class CommentResponse(val id: Long, val postId: Long, val userId: Long, val authorName: String?, val body: String, val createdAt: OffsetDateTime?)

data class SubscriptionResponse(val plan: String, val active: Boolean, val expiresAt: OffsetDateTime?)

data class NotificationResponse(val id: Long, val title: String, val body: String?, val read: Boolean, val createdAt: OffsetDateTime?)

data class VendorAnalyticsResponse(
    val shopId: Long,
    val productCount: Int,
    val orderCount: Int,
    val revenue: java.math.BigDecimal,
    val pendingOffers: Int,
    val reviewsCount: Int,
    val rating: java.math.BigDecimal,
)
