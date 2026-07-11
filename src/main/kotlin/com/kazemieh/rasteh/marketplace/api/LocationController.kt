package com.kazemieh.rasteh.marketplace.api

import com.kazemieh.rasteh.marketplace.application.LocationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/locations")
class LocationController(
    private val locationService: LocationService,
) {
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = locationService.get(id)
}
