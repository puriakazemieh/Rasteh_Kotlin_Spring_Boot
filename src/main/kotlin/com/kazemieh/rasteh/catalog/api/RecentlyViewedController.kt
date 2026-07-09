package com.kazemieh.rasteh.catalog.api

import com.kazemieh.rasteh.catalog.api.dto.PageResponse
import com.kazemieh.rasteh.catalog.api.dto.ProductSummaryResponse
import com.kazemieh.rasteh.catalog.application.RecentlyViewedService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/recently-viewed")
class RecentlyViewedController(
    private val recentlyViewedService: RecentlyViewedService
) {

    @PostMapping("/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun record(@AuthenticationPrincipal principal: UserPrincipal, @PathVariable productId: Long) {
        recentlyViewedService.recordView(principal.id, productId)
    }

    @GetMapping
    fun list(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): PageResponse<ProductSummaryResponse> =
        recentlyViewedService.getRecentlyViewed(principal.id, page, size)
}
