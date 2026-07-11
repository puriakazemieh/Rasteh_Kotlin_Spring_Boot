package com.kazemieh.rasteh.marketplace.api.mapper

import com.kazemieh.rasteh.marketplace.api.dto.LocationResponse
import com.kazemieh.rasteh.marketplace.persistence.entity.LocationEntity

object LocationMapper {
    fun toResponse(l: LocationEntity) = LocationResponse(
        id = l.id,
        cityId = l.city?.id,
        cityName = l.city?.name,
        name = l.name,
        kind = l.kind.name,
        address = l.address,
        floorCount = l.floorCount,
        mapImageUrl = l.mapImageUrl,
        lat = l.lat,
        lng = l.lng,
    )
}
