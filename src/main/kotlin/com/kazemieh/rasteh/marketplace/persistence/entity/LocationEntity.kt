package com.kazemieh.rasteh.marketplace.persistence.entity

import com.kazemieh.rasteh.marketplace.domain.LocationKind
import jakarta.persistence.*

/** محل — پاساژ/بازار/راستهٔ فیزیکی (مثلِ یافت‌آباد، علاءالدین). */
@Entity
@Table(name = "locations")
class LocationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    var city: CityEntity? = null,

    @Column(nullable = false, length = 180)
    var name: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var kind: LocationKind = LocationKind.PASSAGE,

    @Column(length = 255)
    var address: String? = null,

    @Column(name = "floor_count", nullable = false)
    var floorCount: Int = 1,

    @Column(name = "map_image_url", columnDefinition = "text")
    var mapImageUrl: String? = null,

    @Column
    var lat: Double? = null,

    @Column
    var lng: Double? = null,
)
