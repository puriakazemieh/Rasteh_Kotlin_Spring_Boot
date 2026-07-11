package com.kazemieh.rasteh.marketplace.persistence.entity

import com.kazemieh.rasteh.identity.persistence.entity.UserEntity
import com.kazemieh.rasteh.marketplace.domain.ShopStatus
import com.kazemieh.rasteh.marketplace.domain.ShopType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.OffsetDateTime

/** فروشگاه (ونـدور) — متعلق به یک محل و یک راسته، با مالکِ کاربر. */
@Entity
@Table(name = "shops")
class ShopEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    var location: LocationEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rasteh_id")
    var rasteh: RastehEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    var owner: UserEntity? = null,

    @Column(nullable = false, length = 180)
    var name: String = "",

    @Column(length = 120)
    var category: String? = null,

    @Column(length = 40)
    var floor: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var type: ShopType = ShopType.BUYABLE,

    @Column(nullable = false)
    var verified: Boolean = false,

    @Column(nullable = false, precision = 3, scale = 2)
    var rating: BigDecimal = BigDecimal.ZERO,

    @Column(name = "reviews_count", nullable = false)
    var reviewsCount: Int = 0,

    @Column(name = "sales_count", nullable = false)
    var salesCount: Int = 0,

    @Column(length = 30)
    var phone: String? = null,

    @Column(name = "has_chat", nullable = false)
    var hasChat: Boolean = true,

    @Column(name = "accepts_offers", nullable = false)
    var acceptsOffers: Boolean = false,

    @Column(columnDefinition = "text")
    var about: String? = null,

    /** ساعاتِ کاری به‌صورتِ JSON متنی (پارس در کلاینت). */
    @Column(name = "working_hours_json", columnDefinition = "text")
    var workingHoursJson: String? = null,

    @Column(length = 255)
    var address: String? = null,

    @Column(name = "map_x")
    var mapX: Double? = null,

    @Column(name = "map_y")
    var mapY: Double? = null,

    @Column(length = 16)
    var emoji: String? = null,

    @Column(name = "cover_style", length = 120)
    var coverStyle: String? = null,

    @Column(name = "cover_url", columnDefinition = "text")
    var coverUrl: String? = null,

    @Column(name = "logo_url", columnDefinition = "text")
    var logoUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: ShopStatus = ShopStatus.PENDING,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime? = null,

    @Column(name = "approved_at")
    var approvedAt: OffsetDateTime? = null,
)
