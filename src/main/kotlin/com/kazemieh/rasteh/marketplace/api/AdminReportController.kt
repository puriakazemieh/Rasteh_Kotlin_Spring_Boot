package com.kazemieh.rasteh.marketplace.api

import com.kazemieh.rasteh.marketplace.api.dto.ResolveReportRequest
import com.kazemieh.rasteh.marketplace.application.ReportService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

/** رسیدگی به گزارش‌ها (ادمین). */
@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
class AdminReportController(private val service: ReportService) {
    @GetMapping
    fun list(@RequestParam(required = false) status: String?) = service.listAll(status)

    @PostMapping("/{id}/resolve")
    fun resolve(@PathVariable id: Long, @RequestBody req: ResolveReportRequest): ResponseEntity<Any> {
        val res = service.resolve(id, req.status) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(res)
    }
}
