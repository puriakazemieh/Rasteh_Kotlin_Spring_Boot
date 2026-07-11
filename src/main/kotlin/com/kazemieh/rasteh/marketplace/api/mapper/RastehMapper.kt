package com.kazemieh.rasteh.marketplace.api.mapper

import com.kazemieh.rasteh.marketplace.api.dto.RastehResponse
import com.kazemieh.rasteh.marketplace.persistence.entity.RastehEntity

object RastehMapper {
    fun toResponse(r: RastehEntity) = RastehResponse(
        id = r.id,
        label = r.label,
        colorOklch = r.colorOklch,
        iconKey = r.iconKey,
        sortOrder = r.sortOrder,
    )
}
