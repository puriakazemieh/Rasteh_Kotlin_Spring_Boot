package com.kazemieh.rasteh.canonical.api

import com.kazemieh.rasteh.canonical.application.CatalogMergeService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

data class CatalogMergeRequest(val sourceProductId: UUID, val targetProductId: UUID)

@RestController
@RequestMapping("/api/admin/catalog-merges")
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
class CatalogMergeAdminController(private val merges: CatalogMergeService) {
    @GetMapping fun pending() = merges.pending()
    @PostMapping fun request(@AuthenticationPrincipal principal: UserPrincipal, @RequestBody body: CatalogMergeRequest) =
        merges.request(principal.id, body.sourceProductId, body.targetProductId)
    @PostMapping("/{id}/approve") fun approve(@AuthenticationPrincipal principal: UserPrincipal, @PathVariable id: UUID) = merges.review(principal.id, id, true)
    @PostMapping("/{id}/reject") fun reject(@AuthenticationPrincipal principal: UserPrincipal, @PathVariable id: UUID) = merges.review(principal.id, id, false)
}
