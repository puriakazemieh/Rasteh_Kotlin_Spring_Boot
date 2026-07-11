package com.kazemieh.rasteh.marketplace.application

import com.kazemieh.rasteh.marketplace.api.mapper.RastehMapper
import com.kazemieh.rasteh.marketplace.persistence.RastehRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RastehService(
    private val rastehRepository: RastehRepository,
) {
    @Transactional(readOnly = true)
    fun list() =
        rastehRepository.findAllByOrderBySortOrderAscIdAsc().map(RastehMapper::toResponse)
}
