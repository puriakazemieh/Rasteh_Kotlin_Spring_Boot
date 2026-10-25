package com.kazemieh.rasteh.marketplace.api

import com.kazemieh.rasteh.marketplace.application.ShopService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/shops")
class ShopController(
    private val shopService: ShopService,
) {
    /**
     * Retired write path. Shop activation must originate in the application/claim
     * pipeline; otherwise a pending legacy row could bypass consent and review.
     */
    @PostMapping
    fun register(
        @AuthenticationPrincipal principal: UserPrincipal,
    ): Nothing = throw ResponseStatusException(HttpStatus.GONE, "Use /api/shop-applications")

    /** فروشگاه‌هایِ خودم (پنلِ ونـدور). */
    @GetMapping("/mine")
    fun mine(@AuthenticationPrincipal principal: UserPrincipal) =
        shopService.mine(principal.id)

    /** نمای عمومیِ فروشگاه (فقط تأییدشده‌ها). */
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = shopService.getPublic(id)
}
