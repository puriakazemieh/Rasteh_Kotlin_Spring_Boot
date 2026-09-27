package com.kazemieh.rasteh.canonical.api

import com.kazemieh.rasteh.canonical.application.CanonicalBackfillDryRunService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/canonical-marketplace")
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
class CanonicalBackfillAdminController(
    private val dryRunService: CanonicalBackfillDryRunService,
    private val backfillService: com.kazemieh.rasteh.canonical.application.CanonicalMarketplaceBackfillService,
) {
    @GetMapping("/backfill-dry-run")
    fun backfillDryRun() = dryRunService.report()

    @PostMapping("/backfill")
    fun backfill() = backfillService.backfill()
}
