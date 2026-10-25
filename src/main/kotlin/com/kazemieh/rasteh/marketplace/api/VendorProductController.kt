package com.kazemieh.rasteh.marketplace.api

import com.kazemieh.rasteh.marketplace.api.dto.CreateProductRequest
import com.kazemieh.rasteh.marketplace.api.dto.UpdateProductRequest
import com.kazemieh.rasteh.marketplace.application.ShopProductService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

/** مدیریتِ کالاها توسطِ ونـدور (مالکِ فروشگاه) یا ادمین. */
@RestController
@RequestMapping("/api/vendor/products")
class VendorProductController(
    private val productService: ShopProductService,
) {

    /** کالاهایِ فروشگاهِ خودم (شاملِ غیرفعال) — manageListings. */
    @GetMapping("/shop/{shopId}")
    fun listMine(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable shopId: Long,
    ) = productService.listVendorByShop(principal.id, false, shopId)

    @PostMapping
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody req: CreateProductRequest,
    ): Nothing = retired()

    @PutMapping("/{id}")
    fun update(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
        @RequestBody req: UpdateProductRequest,
    ): Nothing = retired()

    @DeleteMapping("/{id}")
    fun delete(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
    ): Nothing = retired()

    /** Phase 7 keeps legacy products readable only while clients migrate to canonical offers. */
    private fun retired(): Nothing = throw ResponseStatusException(
        HttpStatus.GONE,
        "Legacy product writes are retired; use /api/v1/offers",
    )
}
