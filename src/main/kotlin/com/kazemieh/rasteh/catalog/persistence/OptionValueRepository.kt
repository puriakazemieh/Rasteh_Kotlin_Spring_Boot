package com.kazemieh.rasteh.catalog.persistence

import com.kazemieh.rasteh.catalog.persistence.entity.OptionValueEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OptionValueRepository : JpaRepository<OptionValueEntity, Long> {
    fun findByOptionTypeIdAndValue(optionTypeId: Long, value: String): Optional<OptionValueEntity>
}
