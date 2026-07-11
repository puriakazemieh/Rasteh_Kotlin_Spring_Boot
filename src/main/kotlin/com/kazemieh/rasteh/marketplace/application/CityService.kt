package com.kazemieh.rasteh.marketplace.application

import com.kazemieh.rasteh.marketplace.api.mapper.CityMapper
import com.kazemieh.rasteh.marketplace.persistence.CityRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CityService(
    private val cityRepository: CityRepository,
) {
    @Transactional(readOnly = true)
    fun list(query: String?) =
        (if (query.isNullOrBlank())
            cityRepository.findAllByIsActiveTrueOrderByName()
        else
            cityRepository.findAllByIsActiveTrueAndNameContainingIgnoreCaseOrderByName(query.trim()))
            .map(CityMapper::toResponse)
}
