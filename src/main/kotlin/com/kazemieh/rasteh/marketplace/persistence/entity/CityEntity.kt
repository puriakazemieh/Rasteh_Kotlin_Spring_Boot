package com.kazemieh.rasteh.marketplace.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "cities")
class CityEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, length = 120)
    var name: String = "",

    @Column(length = 120)
    var province: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,
)
