package com.kazemieh.rasteh.marketplace.api

import com.kazemieh.rasteh.marketplace.application.SearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/** جست‌وجویِ عمومیِ فروشگاه/کالا (صفحهٔ search). */
@RestController
@RequestMapping("/api/search")
class SearchController(
    private val searchService: SearchService,
) {
    @GetMapping("/shops")
    fun shops(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) rastehId: Long?,
        @RequestParam(required = false) locationId: Long?,
        @RequestParam(required = false) type: String?,
        @RequestParam(required = false) sort: String?,
    ) = searchService.searchShops(query, rastehId, locationId, type, sort)

    @GetMapping("/products")
    fun products(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) shopId: Long?,
        @RequestParam(required = false) condition: String?,
        @RequestParam(required = false) minPrice: Long?,
        @RequestParam(required = false) maxPrice: Long?,
        @RequestParam(required = false) sort: String?,
    ) = searchService.searchProducts(query, shopId, condition, minPrice, maxPrice, sort)
}
