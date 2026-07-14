package com.kazemieh.rasteh.marketplace.api

import com.kazemieh.rasteh.marketplace.api.dto.CreateLocationRequest
import com.kazemieh.rasteh.marketplace.api.dto.CreateRastehRequest
import com.kazemieh.rasteh.marketplace.application.AdminMarketplaceService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

/** مدیریتِ راسته/محل (ادمین). */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
class AdminMarketplaceController(private val service: AdminMarketplaceService) {
    @PostMapping("/rastehs")
    fun createRasteh(@Valid @RequestBody req: CreateRastehRequest) = service.createRasteh(req)

    @PutMapping("/rastehs/{id}")
    fun updateRasteh(@PathVariable id: Long, @Valid @RequestBody req: CreateRastehRequest) = service.updateRasteh(id, req)

    @PostMapping("/locations")
    fun createLocation(@Valid @RequestBody req: CreateLocationRequest) = service.createLocation(req)

    @PostMapping("/rastehs/{rastehId}/locations/{locationId}")
    fun mapLocation(@PathVariable rastehId: Long, @PathVariable locationId: Long): ResponseEntity<Void> {
        service.mapLocation(rastehId, locationId)
        return ResponseEntity.noContent().build()
    }
}
