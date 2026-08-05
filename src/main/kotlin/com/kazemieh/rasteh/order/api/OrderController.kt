package com.kazemieh.rasteh.order.api

import com.kazemieh.rasteh.order.api.dto.AdminUpdateShippingRequest
import com.kazemieh.rasteh.order.api.dto.CreateOrderRequest
import com.kazemieh.rasteh.order.application.OrderService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/shop-orders")
class OrderController(
    private val orderService: OrderService
) {

    @GetMapping
    fun list(@AuthenticationPrincipal principal: UserPrincipal) =
        orderService.listMyOrders(principal.id)

    @GetMapping("/{id}")
    fun get(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long
    ) = orderService.getMyOrder(principal.id, id)

    @PostMapping
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody req: CreateOrderRequest
    ) = orderService.create(principal.id, req)

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancel(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long
    ) {
        orderService.cancelMyOrder(principal.id, id)
    }

    @PatchMapping("/{id}/shipping")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateShipping(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
        @Valid @RequestBody req: AdminUpdateShippingRequest
    ) {
        orderService.updateShipping(id, req)
    }

    @GetMapping("/{id}/track")
    fun track(@AuthenticationPrincipal principal: UserPrincipal, @PathVariable id: Long) =
        orderService.trackMyOrder(principal.id, id)
}
