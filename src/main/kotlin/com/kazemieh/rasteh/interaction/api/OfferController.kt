package com.kazemieh.rasteh.interaction.api

import com.kazemieh.rasteh.interaction.api.dto.CreateOfferRequest
import com.kazemieh.rasteh.interaction.application.OfferService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/offers")
class OfferController(
    private val offerService: OfferService,
) {
    private fun UserPrincipal.isAdmin(): Boolean =
        authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_SUPERADMIN" }

    /** ثبتِ پیشنهادِ قیمت (خریدار). */
    @PostMapping
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody req: CreateOfferRequest,
    ) = offerService.create(principal.id, req)

    /** پیشنهادهایِ من (خریدار). */
    @GetMapping("/mine")
    fun mine(@AuthenticationPrincipal principal: UserPrincipal) =
        offerService.listMine(principal.id)

    /** پیشنهادهایِ یک فروشگاه (فروشنده/ادمین). */
    @GetMapping("/shop/{shopId}")
    fun forShop(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable shopId: Long,
    ) = offerService.listForShop(principal.id, principal.isAdmin(), shopId)

    @PostMapping("/{id}/accept")
    fun accept(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
    ) = offerService.accept(principal.id, principal.isAdmin(), id)

    @PostMapping("/{id}/reject")
    fun reject(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
    ) = offerService.reject(principal.id, principal.isAdmin(), id)
}
