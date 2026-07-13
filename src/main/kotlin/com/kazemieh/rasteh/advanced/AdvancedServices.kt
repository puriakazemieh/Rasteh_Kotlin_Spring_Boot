package com.kazemieh.rasteh.advanced

import com.kazemieh.rasteh.advanced.entity.CommunityCommentEntity
import com.kazemieh.rasteh.advanced.entity.CommunityPostEntity
import com.kazemieh.rasteh.advanced.entity.NotificationEntity
import com.kazemieh.rasteh.advanced.entity.SubscriptionEntity
import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.interaction.persistence.OfferRepository
import com.kazemieh.rasteh.marketplace.domain.OrderStatus
import com.kazemieh.rasteh.marketplace.order.MarketplaceOrderRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.shared.error.BadRequestException
import com.kazemieh.rasteh.shared.error.ErrorCodes
import com.kazemieh.rasteh.shared.error.ShopAccessDeniedException
import com.kazemieh.rasteh.shared.error.ShopNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.OffsetDateTime

private fun name(u: com.kazemieh.rasteh.identity.persistence.entity.UserEntity?): String? =
    u?.let { listOfNotNull(it.firstName?.trim(), it.lastName?.trim()).joinToString(" ").ifBlank { null } }

@Service
class CommunityService(
    private val postRepository: CommunityPostRepository,
    private val commentRepository: CommunityCommentRepository,
    private val userRepository: UserRepository,
) {
    private fun toPost(p: CommunityPostEntity) = PostResponse(p.id, p.userId, p.authorName, p.body, p.commentCount, p.createdAt)
    private fun toComment(c: CommunityCommentEntity) = CommentResponse(c.id, c.postId, c.userId, c.authorName, c.body, c.createdAt)

    @Transactional(readOnly = true)
    fun listPosts() = postRepository.findAllByOrderByIdDesc().map(::toPost)

    @Transactional
    fun createPost(userId: Long, req: CreatePostRequest): PostResponse {
        val author = name(userRepository.findById(userId).orElse(null))
        return toPost(postRepository.save(CommunityPostEntity(userId = userId, authorName = author, body = req.body.trim())))
    }

    @Transactional(readOnly = true)
    fun listComments(postId: Long) = commentRepository.findAllByPostIdOrderByIdAsc(postId).map(::toComment)

    @Transactional
    fun addComment(userId: Long, postId: Long, req: CreateCommentRequest): CommentResponse {
        val post = postRepository.findById(postId).orElseThrow { BadRequestException("Post not found", ErrorCodes.INVALID_INPUT) }
        val author = name(userRepository.findById(userId).orElse(null))
        val comment = commentRepository.save(CommunityCommentEntity(postId = postId, userId = userId, authorName = author, body = req.body.trim()))
        post.commentCount += 1
        return toComment(comment)
    }
}

@Service
class SubscriptionService(
    private val repository: SubscriptionRepository,
) {
    @Transactional
    fun me(userId: Long): SubscriptionResponse {
        val sub = repository.findByUserId(userId) ?: repository.save(SubscriptionEntity(userId = userId, plan = "PLUS", active = false))
        val active = sub.active && (sub.expiresAt?.isAfter(OffsetDateTime.now()) ?: false)
        return SubscriptionResponse(sub.plan, active, sub.expiresAt)
    }

    @Transactional
    fun subscribe(userId: Long): SubscriptionResponse {
        val sub = repository.findByUserId(userId) ?: repository.save(SubscriptionEntity(userId = userId))
        sub.active = true
        sub.plan = "PLUS"
        sub.expiresAt = OffsetDateTime.now().plusDays(30)
        return SubscriptionResponse(sub.plan, true, sub.expiresAt)
    }
}

@Service
class NotificationService(
    private val repository: NotificationRepository,
) {
    private fun toResponse(n: NotificationEntity) = NotificationResponse(n.id, n.title, n.body, n.read, n.createdAt)

    @Transactional(readOnly = true)
    fun listMine(userId: Long) = repository.findAllByUserIdOrderByIdDesc(userId).map(::toResponse)

    @Transactional
    fun markRead(userId: Long, id: Long) {
        val n = repository.findById(id).orElse(null) ?: return
        if (n.userId == userId) n.read = true
    }
}

@Service
class VendorAnalyticsService(
    private val shopRepository: ShopRepository,
    private val productRepository: ShopProductRepository,
    private val orderRepository: MarketplaceOrderRepository,
    private val offerRepository: OfferRepository,
) {
    @Transactional(readOnly = true)
    fun forShop(userId: Long, isAdmin: Boolean, shopId: Long): VendorAnalyticsResponse {
        val shop = shopRepository.findById(shopId).orElseThrow { ShopNotFoundException(shopId) }
        if (!isAdmin && shop.owner?.id != userId) throw ShopAccessDeniedException(shopId)
        val orders = orderRepository.findAllByShopIdOrderByIdDesc(shopId)
        val revenue = orders.filter { it.status != OrderStatus.CANCELLED }.fold(BigDecimal.ZERO) { acc, o -> acc.add(o.totalAmount) }
        val products = productRepository.findAllByShopIdOrderByIdDesc(shopId)
        val pendingOffers = offerRepository.findAllByShopIdOrderByIdDesc(shopId).count { it.status.name == "PENDING" }
        return VendorAnalyticsResponse(
            shopId = shopId,
            productCount = products.size,
            orderCount = orders.size,
            revenue = revenue,
            pendingOffers = pendingOffers,
            reviewsCount = shop.reviewsCount,
            rating = shop.rating,
        )
    }
}
