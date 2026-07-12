package com.kazemieh.rasteh.marketplace.order.entity

import com.kazemieh.rasteh.identity.persistence.entity.UserEntity
import com.kazemieh.rasteh.marketplace.domain.OrderStatus
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopEntity
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.OffsetDateTime

/** سفارشِ تک‌ونـدوری — همهٔ اقلام از یک فروشگاه. */
@Entity
@Table(name = "marketplace_orders")
class MarketplaceOrderEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    var shop: ShopEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_user_id", nullable = false)
    var customer: UserEntity? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: OrderStatus = OrderStatus.PENDING,

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 0)
    var totalAmount: BigDecimal = BigDecimal.ZERO,

    @Column(columnDefinition = "text")
    var note: String? = null,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var items: MutableList<MarketplaceOrderItemEntity> = mutableListOf(),

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,
)
