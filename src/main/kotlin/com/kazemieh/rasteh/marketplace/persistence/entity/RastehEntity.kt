package com.kazemieh.rasteh.marketplace.persistence.entity

import jakarta.persistence.*

/**
 * راسته — صنف (مبل/موبایل/طلا/…). محورِ اصلیِ کشف در خانهٔ v2.
 * رابطهٔ چند-به-چند با محل‌ها: هر راسته در چند محل هست و هر محل چند راسته دارد.
 */
@Entity
@Table(name = "rastehs")
class RastehEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, length = 120)
    var label: String = "",

    /** رنگِ آیکونِ راسته به‌صورتِ رشتهٔ oklch (کلاینت مستقیم مصرف می‌کند). */
    @Column(name = "color_oklch", length = 60)
    var colorOklch: String? = null,

    /** کلیدِ آیکون (کلاینت به آیکونِ محلی نگاشت می‌کند). */
    @Column(name = "icon_key", length = 60)
    var iconKey: String? = null,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rasteh_locations",
        joinColumns = [JoinColumn(name = "rasteh_id")],
        inverseJoinColumns = [JoinColumn(name = "location_id")]
    )
    var locations: MutableSet<LocationEntity> = mutableSetOf(),
)
