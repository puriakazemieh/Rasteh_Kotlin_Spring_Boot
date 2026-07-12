package com.kazemieh.rasteh.marketplace.api

import com.kazemieh.rasteh.marketplace.api.dto.CreateProductRequest
import com.kazemieh.rasteh.marketplace.api.dto.UpdateProductRequest
import com.kazemieh.rasteh.marketplace.application.ShopProductService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

/** مدیریتِ کالاها توسطِ ونـدور (مالکِ فروشگاه) یا ادمین. */
@RestController
@RequestMapping("/api/vendor/products")
class VendorProductController(
    private val productService: ShopProductService,
) {
    private fun UserPrincipal.isAdmin(): Boolean =
        authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_SUPERADMIN" }

    /** کالاهایِ فروشگاهِ خودم (شاملِ غیرفعال) — manageListings. */
    @GetMapping("/shop/{shopId}")
    fun listMine(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable shopId: Long,
    ) = productService.listVendorByShop(principal.id, principal.isAdmin(), shopId)

    @PostMapping
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody req: CreateProductRequest,
    ) = productService.create(principal.id, principal.isAdmin(), req)

    @PutMapping("/{id}")
    fun update(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
        @RequestBody req: UpdateProductRequest,
    ) = productService.update(principal.id, principal.isAdmin(), id, req)

    @DeleteMapping("/{id}")
    fun delete(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        productService.delete(principal.id, principal.isAdmin(), id)
        return ResponseEntity.noContent().build()
    }
}
