package com.kazemieh.rasteh.interaction.persistence.entity

import com.kazemieh.rasteh.identity.persistence.entity.UserEntity
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopEntity
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

/** گفت‌وگویِ خریدار↔فروشگاه («پیام به فروشگاه» در shopDetail). یکتا به‌ازای (customer, shop). */
@Entity
@Table(
    name = "conversations",
    uniqueConstraints = [UniqueConstraint(name = "uq_conversation_customer_shop", columnNames = ["customer_user_id", "shop_id"])]
)
class ConversationEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_user_id", nullable = false)
    var customer: UserEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    var shop: ShopEntity? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,

    @Column(name = "last_message_at")
    var lastMessageAt: OffsetDateTime? = null,

    @Column(name = "last_message_preview", length = 200)
    var lastMessagePreview: String? = null,
)
