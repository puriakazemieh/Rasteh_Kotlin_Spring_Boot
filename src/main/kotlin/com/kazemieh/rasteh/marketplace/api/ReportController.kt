package com.kazemieh.rasteh.marketplace.api

import com.kazemieh.rasteh.marketplace.api.dto.CreateReportRequest
import com.kazemieh.rasteh.marketplace.application.ReportService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

/** ثبتِ گزارشِ تخلف (کاربرِ احرازشده). */
@RestController
@RequestMapping("/api/reports")
class ReportController(private val service: ReportService) {
    @PostMapping
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody req: CreateReportRequest,
    ) = service.create(principal.id, req)
}
