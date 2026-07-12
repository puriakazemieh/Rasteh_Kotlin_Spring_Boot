package com.kazemieh.rasteh.marketplace.order

import com.kazemieh.rasteh.shared.security.UserPrincipal
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** سفارش‌هایِ یک فروشگاه برایِ فروشنده/ادمین (vendorOrders). */
@RestController
@RequestMapping("/api/vendor/orders")
class VendorOrderController(
    private val orderService: MarketplaceOrderService,
) {
    private fun UserPrincipal.isAdmin(): Boolean =
        authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_SUPERADMIN" }

    @GetMapping("/shop/{shopId}")
    fun forShop(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable shopId: Long,
    ) = orderService.listForShop(principal.id, principal.isAdmin(), shopId)
}
