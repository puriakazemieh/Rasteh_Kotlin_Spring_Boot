package com.kazemieh.rasteh.marketplace.api

import com.kazemieh.rasteh.marketplace.application.LocationService
import com.kazemieh.rasteh.marketplace.application.ShopService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/locations")
class LocationController(
    private val locationService: LocationService,
    private val shopService: ShopService,
) {
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = locationService.get(id)

    /** فروشگاه‌هایِ تأییدشدهٔ این محل (rastehSearch) — فیلترِ اختیاریِ راسته. */
    @GetMapping("/{id}/shops")
    fun shops(
        @PathVariable id: Long,
        @RequestParam(required = false) rastehId: Long?,
    ) = shopService.listByLocation(id, rastehId)
}
