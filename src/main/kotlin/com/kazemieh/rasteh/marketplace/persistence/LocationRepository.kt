package com.kazemieh.rasteh.marketplace.persistence

import com.kazemieh.rasteh.marketplace.persistence.entity.LocationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface LocationRepository : JpaRepository<LocationEntity, Long> {

    fun findAllByCityIdOrderByName(cityId: Long): List<LocationEntity>

    /** محل‌هایِ یک راسته (باتم‌شیتِ انتخابِ محل). */
    @Query("select l from RastehEntity r join r.locations l where r.id = :rastehId order by l.name")
    fun findAllByRastehId(@Param("rastehId") rastehId: Long): List<LocationEntity>
}
