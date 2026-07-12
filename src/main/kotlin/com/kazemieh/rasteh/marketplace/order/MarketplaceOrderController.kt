package com.kazemieh.rasteh.marketplace.order

import com.kazemieh.rasteh.shared.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

/** سفارش‌ها (خریدار) + به‌روزرسانیِ وضعیت. اقلامِ سبد سمتِ کلاینت مدیریت می‌شوند. */
@RestController
@RequestMapping("/api/orders")
class MarketplaceOrderController(
    private val orderService: MarketplaceOrderService,
) {
    private fun UserPrincipal.isAdmin(): Boolean =
        authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_SUPERADMIN" }

    /** ثبتِ سفارش (checkout). */
    @PostMapping
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody req: CreateOrderRequest,
    ) = orderService.create(principal.id, req)

    /** سفارش‌های من (خریدار). */
    @GetMapping("/mine")
    fun mine(@AuthenticationPrincipal principal: UserPrincipal) =
        orderService.listMine(principal.id)

    @GetMapping("/{id}")
    fun get(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
    ) = orderService.get(principal.id, principal.isAdmin(), id)

    @PostMapping("/{id}/status")
    fun updateStatus(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
        @Valid @RequestBody req: UpdateOrderStatusRequest,
    ) = orderService.updateStatus(principal.id, principal.isAdmin(), id, req.status)
}
