package com.kazemieh.rasteh.canonical.api

import com.kazemieh.rasteh.canonical.application.CanonicalOfferReadService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/** Versioned, read-only surface while legacy writes are still in the compatibility window. */
@RestController
@RequestMapping("/api/v1/offers")
class CanonicalOfferController(
    private val canonicalOfferReadService: CanonicalOfferReadService,
) {
    @GetMapping
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ) = canonicalOfferReadService.list(page, size)

    @GetMapping("/{id}")
    fun get(@PathVariable id: String) = canonicalOfferReadService.get(id)
}
