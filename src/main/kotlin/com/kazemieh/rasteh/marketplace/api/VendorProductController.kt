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
    ) = productService.create(principal.id, false, req)

    @PutMapping("/{id}")
    fun update(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
        @RequestBody req: UpdateProductRequest,
    ) = productService.update(principal.id, false, id, req)

    @DeleteMapping("/{id}")
    fun delete(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        productService.delete(principal.id, false, id)
        return ResponseEntity.noContent().build()
    }
}
