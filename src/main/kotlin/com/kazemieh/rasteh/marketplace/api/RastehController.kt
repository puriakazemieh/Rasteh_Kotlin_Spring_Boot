package com.kazemieh.rasteh.marketplace.api

import com.kazemieh.rasteh.marketplace.application.LocationService
import com.kazemieh.rasteh.marketplace.application.RastehService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/rastehs")
class RastehController(
    private val rastehService: RastehService,
    private val locationService: LocationService,
) {
    /** گریدِ راسته‌هایِ خانه. */
    @GetMapping
    fun list() = rastehService.list()

    /** محل‌هایِ یک راسته — باتم‌شیتِ انتخابِ محل. */
    @GetMapping("/{id}/locations")
    fun locations(@PathVariable id: Long) = locationService.listByRasteh(id)
}
