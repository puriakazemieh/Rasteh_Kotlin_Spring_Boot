package com.kazemieh.rasteh.marketplace.persistence.entity

import com.kazemieh.rasteh.marketplace.domain.ProductCondition
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.OffsetDateTime

/**
 * کالا/آگهیِ یک فروشگاه. در فروشگاه‌هایِ BUYABLE قابلِ خرید است؛ در VISIT_ONLY صرفاً ویترین.
 * قابلِ خرید بودن (`purchasable`) در mapper از روی نوعِ فروشگاه + موجودی مشتق می‌شود.
 */
@Entity
@Table(name = "shop_products")
class ShopProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    var shop: ShopEntity? = null,

    @Column(nullable = false, length = 180)
    var name: String = "",

    @Column(columnDefinition = "text")
    var description: String? = null,

    @Column(nullable = false, precision = 15, scale = 0)
    var price: BigDecimal = BigDecimal.ZERO,

    @Column(name = "old_price", precision = 15, scale = 0)
    var oldPrice: BigDecimal? = null,

    @Column(name = "discount_percent")
    var discountPercent: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var condition: ProductCondition = ProductCondition.NEW,

    @Column(nullable = false)
    var stock: Int = 0,

    @Column(name = "category_name", length = 120)
    var categoryName: String? = null,

    @Column(length = 16)
    var emoji: String? = null,

    @Column(name = "image_url", columnDefinition = "text")
    var imageUrl: String? = null,

    @Column(nullable = false)
    var active: Boolean = true,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,
)
