package com.kazemieh.rasteh.marketplace.persistence.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

/** نظرِ کاربر روی یک فروشگاه (تبِ نظراتِ shopDetail). یک نظر به‌ازای هر کاربر/فروشگاه. */
@Entity
@Table(
    name = "reviews",
    uniqueConstraints = [UniqueConstraint(name = "uq_review_user_shop", columnNames = ["user_id", "shop_id"])]
)
class ReviewEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    var shop: ShopEntity? = null,

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,

    @Column(name = "author_name", length = 120)
    var authorName: String? = null,

    @Column(nullable = false)
    var rating: Int = 5,

    @Column(columnDefinition = "text")
    var comment: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,
)
