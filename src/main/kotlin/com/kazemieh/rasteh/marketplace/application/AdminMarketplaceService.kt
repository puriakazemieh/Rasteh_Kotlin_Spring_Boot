package com.kazemieh.rasteh.marketplace.application

import com.kazemieh.rasteh.marketplace.api.dto.CreateLocationRequest
import com.kazemieh.rasteh.marketplace.api.dto.CreateRastehRequest
import com.kazemieh.rasteh.marketplace.api.mapper.LocationMapper
import com.kazemieh.rasteh.marketplace.api.mapper.RastehMapper
import com.kazemieh.rasteh.marketplace.domain.LocationKind
import com.kazemieh.rasteh.marketplace.persistence.CityRepository
import com.kazemieh.rasteh.marketplace.persistence.LocationRepository
import com.kazemieh.rasteh.marketplace.persistence.RastehRepository
import com.kazemieh.rasteh.marketplace.persistence.entity.LocationEntity
import com.kazemieh.rasteh.marketplace.persistence.entity.RastehEntity
import com.kazemieh.rasteh.shared.error.CityNotFoundException
import com.kazemieh.rasteh.shared.error.LocationNotFoundException
import com.kazemieh.rasteh.shared.error.RastehNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/** مدیریتِ راسته/محل توسطِ ادمین (پنلِ ادمین). */
@Service
class AdminMarketplaceService(
    private val rastehRepository: RastehRepository,
    private val locationRepository: LocationRepository,
    private val cityRepository: CityRepository,
) {
    @Transactional
    fun createRasteh(req: CreateRastehRequest) = RastehMapper.toResponse(
        rastehRepository.save(
            RastehEntity(
                label = req.label.trim(),
                colorOklch = req.colorOklch?.trim(),
                iconKey = req.iconKey?.trim(),
                sortOrder = req.sortOrder,
            )
        )
    )

    @Transactional
    fun updateRasteh(id: Long, req: CreateRastehRequest) = RastehMapper.toResponse(
        rastehRepository.findById(id).orElseThrow { RastehNotFoundException(id) }.also {
            it.label = req.label.trim()
            it.colorOklch = req.colorOklch?.trim()
            it.iconKey = req.iconKey?.trim()
            it.sortOrder = req.sortOrder
        }
    )

    @Transactional
    fun createLocation(req: CreateLocationRequest): com.kazemieh.rasteh.marketplace.api.dto.LocationResponse {
        val city = cityRepository.findById(req.cityId).orElseThrow { CityNotFoundException(req.cityId) }
        val kind = runCatching { LocationKind.valueOf(req.kind.trim().uppercase()) }.getOrDefault(LocationKind.PASSAGE)
        return LocationMapper.toResponse(
            locationRepository.save(
                LocationEntity(city = city, name = req.name.trim(), kind = kind, address = req.address?.trim(), floorCount = req.floorCount)
            )
        )
    }

    /** نگاشتِ یک محل به یک راسته (رابطهٔ چند-به-چند). */
    @Transactional
    fun mapLocation(rastehId: Long, locationId: Long) {
        val rasteh = rastehRepository.findById(rastehId).orElseThrow { RastehNotFoundException(rastehId) }
        val location = locationRepository.findById(locationId).orElseThrow { LocationNotFoundException(locationId) }
        rasteh.locations.add(location)
    }
}
