package com.kazemieh.rasteh.marketplace.api

import com.kazemieh.rasteh.marketplace.api.dto.CreateShopRequest
import com.kazemieh.rasteh.marketplace.application.ShopService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/shops")
class ShopController(
    private val shopService: ShopService,
) {
    /** ثبتِ درخواستِ فروشگاه (becomeVendor) — کاربرِ احرازشده. */
    @PostMapping
    fun register(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody req: CreateShopRequest,
    ) = shopService.register(principal.id, req)

    /** فروشگاه‌هایِ خودم (پنلِ ونـدور). */
    @GetMapping("/mine")
    fun mine(@AuthenticationPrincipal principal: UserPrincipal) =
        shopService.mine(principal.id)

    /** نمای عمومیِ فروشگاه (فقط تأییدشده‌ها). */
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = shopService.getPublic(id)
}
