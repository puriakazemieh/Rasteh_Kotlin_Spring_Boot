package com.kazemieh.rasteh.marketplace.persistence

import com.kazemieh.rasteh.marketplace.persistence.entity.ReportEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ReportRepository : JpaRepository<ReportEntity, Long> {
    fun findAllByStatusOrderByIdDesc(status: String): List<ReportEntity>
    fun findAllByOrderByIdDesc(): List<ReportEntity>
}
