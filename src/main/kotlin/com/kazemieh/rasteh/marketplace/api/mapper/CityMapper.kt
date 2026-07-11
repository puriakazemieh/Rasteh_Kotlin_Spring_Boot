package com.kazemieh.rasteh.marketplace.api.mapper

import com.kazemieh.rasteh.marketplace.api.dto.CityResponse
import com.kazemieh.rasteh.marketplace.persistence.entity.CityEntity

object CityMapper {
    fun toResponse(c: CityEntity) = CityResponse(
        id = c.id,
        name = c.name,
        province = c.province,
    )
}
