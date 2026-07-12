package com.kazemieh.rasteh.interaction.persistence.entity

import com.kazemieh.rasteh.identity.persistence.entity.UserEntity
import com.kazemieh.rasteh.interaction.domain.OfferStatus
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopEntity
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopProductEntity
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.OffsetDateTime

/** پیشنهادِ قیمت روی یک فروشگاه/کالا (makeOffer) — فقط اگر فروشگاه پیشنهاد بپذیرد. */
@Entity
@Table(name = "offers")
class OfferEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    var shop: ShopEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: ShopProductEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_user_id", nullable = false)
    var customer: UserEntity? = null,

    @Column(nullable = false, precision = 15, scale = 0)
    var amount: BigDecimal = BigDecimal.ZERO,

    @Column(columnDefinition = "text")
    var message: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: OfferStatus = OfferStatus.PENDING,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,
)
