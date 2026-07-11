package com.kazemieh.rasteh.marketplace.application

import com.kazemieh.rasteh.marketplace.api.mapper.LocationMapper
import com.kazemieh.rasteh.marketplace.persistence.LocationRepository
import com.kazemieh.rasteh.marketplace.persistence.RastehRepository
import com.kazemieh.rasteh.shared.error.LocationNotFoundException
import com.kazemieh.rasteh.shared.error.RastehNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LocationService(
    private val locationRepository: LocationRepository,
    private val rastehRepository: RastehRepository,
) {
    /** محل‌هایِ یک راسته — باتم‌شیتِ انتخابِ محل. */
    @Transactional(readOnly = true)
    fun listByRasteh(rastehId: Long): List<com.kazemieh.rasteh.marketplace.api.dto.LocationResponse> {
        if (!rastehRepository.existsById(rastehId)) throw RastehNotFoundException(rastehId)
        return locationRepository.findAllByRastehId(rastehId).map(LocationMapper::toResponse)
    }

    @Transactional(readOnly = true)
    fun get(locationId: Long) =
        locationRepository.findById(locationId)
            .map(LocationMapper::toResponse)
            .orElseThrow { LocationNotFoundException(locationId) }
}
