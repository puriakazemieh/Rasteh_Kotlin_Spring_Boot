package com.kazemieh.rasteh.marketplace.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.OffsetDateTime

// Report
data class CreateReportRequest(
    @field:NotBlank val targetType: String,   // SHOP / PRODUCT
    @field:NotNull val targetId: Long,
    val reason: String? = null,
)
data class ReportResponse(
    val id: Long, val userId: Long, val targetType: String, val targetId: Long,
    val reason: String?, val status: String, val createdAt: OffsetDateTime?,
)
data class ResolveReportRequest(val status: String)   // RESOLVED / DISMISSED

// Admin rasteh / location management
data class CreateRastehRequest(
    @field:NotBlank val label: String,
    val colorOklch: String? = null,
    val iconKey: String? = null,
    val sortOrder: Int = 0,
)
data class CreateLocationRequest(
    @field:NotNull val cityId: Long,
    @field:NotBlank val name: String,
    val kind: String = "PASSAGE",
    val address: String? = null,
    val floorCount: Int = 1,
)
