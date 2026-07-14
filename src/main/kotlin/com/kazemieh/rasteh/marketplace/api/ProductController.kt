package com.kazemieh.rasteh.marketplace.api

import com.kazemieh.rasteh.marketplace.application.ShopProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/** خواندنِ عمومیِ کالاها (کاتالوگِ فروشگاه‌ها). */
@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ShopProductService,
) {
    /** کالاهایِ یک فروشگاهِ تأییدشده. */
    @GetMapping
    fun listByShop(@RequestParam shopId: Long) = productService.listPublicByShop(shopId)

    /** جزئیاتِ یک کالا. */
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = productService.getPublic(id)

    /** فروشندگانِ دیگرِ همین کالا (ارزان‌ترین اول). */
    @GetMapping("/{id}/other-sellers")
    fun otherSellers(@PathVariable id: Long) = productService.otherSellers(id)
}
