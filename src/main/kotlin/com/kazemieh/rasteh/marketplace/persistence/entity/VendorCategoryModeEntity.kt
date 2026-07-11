package com.kazemieh.rasteh.marketplace.persistence.entity

import com.kazemieh.rasteh.marketplace.domain.CategoryMode
import jakarta.persistence.*

/** حالتِ نمایشِ یک دستهٔ محصولی در یک فروشگاه (ویترینو/کامل). */
@Entity
@Table(
    name = "vendor_category_modes",
    uniqueConstraints = [UniqueConstraint(name = "uq_shop_category", columnNames = ["shop_id", "category_id"])]
)
class VendorCategoryModeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    var shop: ShopEntity? = null,

    @Column(name = "category_id")
    var categoryId: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var mode: CategoryMode = CategoryMode.COMMERCE,
)
