package com.kazemieh.rasteh.marketplace.api.mapper

import com.kazemieh.rasteh.marketplace.api.dto.ShopResponse
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopEntity

object ShopMapper {
    fun toResponse(s: ShopEntity) = ShopResponse(
        id = s.id,
        locationId = s.location?.id,
        locationName = s.location?.name,
        rastehId = s.rasteh?.id,
        rastehLabel = s.rasteh?.label,
        ownerUserId = s.owner?.id,
        name = s.name,
        category = s.category,
        floor = s.floor,
        type = s.type.name,
        verified = s.verified,
        rating = s.rating,
        reviewsCount = s.reviewsCount,
        salesCount = s.salesCount,
        phone = s.phone,
        hasChat = s.hasChat,
        acceptsOffers = s.acceptsOffers,
        about = s.about,
        workingHoursJson = s.workingHoursJson,
        address = s.address,
        mapX = s.mapX,
        mapY = s.mapY,
        emoji = s.emoji,
        coverStyle = s.coverStyle,
        coverUrl = s.coverUrl,
        logoUrl = s.logoUrl,
        status = s.status.name,
        createdAt = s.createdAt,
        approvedAt = s.approvedAt,
    )
}
