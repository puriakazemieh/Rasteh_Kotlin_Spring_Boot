package com.kazemieh.rasteh.marketplace.api

import com.kazemieh.rasteh.marketplace.application.AdminShopService
import com.kazemieh.rasteh.marketplace.domain.ShopStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/shops")
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
class AdminShopController(
    private val adminShopService: AdminShopService,
) {
    /** صفِ تأیید (پیش‌فرض PENDING) — adminDash. */
    @GetMapping
    fun list(@RequestParam(required = false, defaultValue = "PENDING") status: ShopStatus) =
        adminShopService.listByStatus(status)

    @PostMapping("/{id}/approve")
    fun approve(@PathVariable id: Long) = adminShopService.approve(id)

    @PostMapping("/{id}/reject")
    fun reject(@PathVariable id: Long) = adminShopService.reject(id)

    @PostMapping("/{id}/suspend")
    fun suspend(@PathVariable id: Long) = adminShopService.suspend(id)
}
