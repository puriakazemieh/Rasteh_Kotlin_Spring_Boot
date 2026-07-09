package com.kazemieh.rasteh.catalog.persistence

import com.kazemieh.rasteh.catalog.persistence.entity.OptionTypeEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OptionTypeRepository : JpaRepository<OptionTypeEntity, Long> {
    fun findByName(name: String): Optional<OptionTypeEntity>
}
