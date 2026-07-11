package com.kazemieh.rasteh.marketplace.persistence

import com.kazemieh.rasteh.marketplace.persistence.entity.CityEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CityRepository : JpaRepository<CityEntity, Long> {
    fun findAllByIsActiveTrueOrderByName(): List<CityEntity>
    fun findAllByIsActiveTrueAndNameContainingIgnoreCaseOrderByName(name: String): List<CityEntity>
}
