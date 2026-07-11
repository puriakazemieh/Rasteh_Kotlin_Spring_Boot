package com.kazemieh.rasteh.marketplace.api

import com.kazemieh.rasteh.marketplace.application.CityService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/cities")
class CityController(
    private val cityService: CityService,
) {
    @GetMapping
    fun list(@RequestParam(required = false) query: String?) = cityService.list(query)
}
