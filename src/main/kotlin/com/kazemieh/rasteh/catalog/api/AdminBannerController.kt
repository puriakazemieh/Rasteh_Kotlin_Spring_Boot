package com.kazemieh.rasteh.catalog.api

import com.kazemieh.rasteh.catalog.api.dto.BannerResponse
import com.kazemieh.rasteh.catalog.api.dto.BannerUpsertRequest
import com.kazemieh.rasteh.catalog.application.BannerService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/banners")
@PreAuthorize("hasRole('ADMIN')")
class AdminBannerController(
    private val bannerService: BannerService
) {

    @GetMapping
    fun list(): List<BannerResponse> = bannerService.listAll()

    @PostMapping
    fun create(@RequestBody request: BannerUpsertRequest): BannerResponse =
        bannerService.create(request)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: BannerUpsertRequest,
    ): BannerResponse = bannerService.update(id, request)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = bannerService.delete(id)
}
