package com.kazemieh.rasteh.features

import com.kazemieh.rasteh.shared.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

private fun UserPrincipal.isAdmin(): Boolean =
    authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_SUPERADMIN" }

/** فلش (flash) — فهرستِ عمومی + ساختِ ونـدور/ادمین. */
@RestController
@RequestMapping("/api/flash")
class FlashSaleController(private val service: FlashSaleService) {
    @GetMapping
    fun list() = service.listActive()

    @PostMapping
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody req: CreateFlashSaleRequest,
    ) = service.create(principal.id, principal.isAdmin(), req)
}

/** خریدِ گروهی (groupbuy) — فهرست + ساخت + پیوستن. */
@RestController
@RequestMapping("/api/groupbuys")
class GroupBuyController(private val service: GroupBuyService) {
    @GetMapping
    fun list(@AuthenticationPrincipal principal: UserPrincipal?) =
        service.listActive(principal?.id)

    @PostMapping
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody req: CreateGroupBuyRequest,
    ) = service.create(principal.id, principal.isAdmin(), req)

    @PostMapping("/{id}/join")
    fun join(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
    ) = service.join(principal.id, id)
}

/** وفاداری (loyalty). */
@RestController
@RequestMapping("/api/loyalty")
class LoyaltyController(private val service: LoyaltyService) {
    @GetMapping("/me")
    fun me(@AuthenticationPrincipal principal: UserPrincipal) = service.getOrCreate(principal.id)
}

/** هشدارِ قیمت (pricealert). */
@RestController
@RequestMapping("/api/pricealerts")
class PriceAlertController(private val service: PriceAlertService) {
    @GetMapping("/mine")
    fun mine(@AuthenticationPrincipal principal: UserPrincipal) = service.listMine(principal.id)

    @PostMapping
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody req: CreatePriceAlertRequest,
    ) = service.create(principal.id, req)

    @DeleteMapping("/{id}")
    fun delete(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        service.delete(principal.id, id)
        return ResponseEntity.noContent().build()
    }
}
