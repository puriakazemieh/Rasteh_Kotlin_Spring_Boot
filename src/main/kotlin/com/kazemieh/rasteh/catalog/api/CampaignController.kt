package com.kazemieh.rasteh.catalog.api

import com.kazemieh.rasteh.catalog.api.dto.CampaignResponse
import com.kazemieh.rasteh.catalog.application.CampaignService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/campaigns")
class CampaignController(
    private val campaignService: CampaignService
) {

    /** کمپینِ فعالِ جاری؛ ۲۰۴ اگر کمپینی فعال نباشد. */
    @GetMapping("/active")
    fun active(
        @AuthenticationPrincipal principal: UserPrincipal?,
    ): ResponseEntity<CampaignResponse> {
        val campaign = campaignService.getActiveCampaign(principal?.id)
            ?: return ResponseEntity.noContent().build()
        return ResponseEntity.ok(campaign)
    }
}
