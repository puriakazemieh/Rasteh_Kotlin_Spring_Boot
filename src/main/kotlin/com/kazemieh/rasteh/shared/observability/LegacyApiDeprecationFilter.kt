package com.kazemieh.rasteh.shared.observability

import io.micrometer.core.instrument.MeterRegistry
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/** Makes legacy consumption measurable before a future, separately announced freeze. */
@Component
class LegacyApiDeprecationFilter(
    private val meterRegistry: MeterRegistry,
) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val family = legacyFamily(request.requestURI)
        if (family != null) {
            response.setHeader("Deprecation", "true")
            response.addHeader("Link", "</api/v1/offers>; rel=\"successor-version\"")
            meterRegistry.counter("rasteh.legacy_api.request.total", "family", family).increment()
        }
        filterChain.doFilter(request, response)
    }

    private fun legacyFamily(uri: String): String? = when {
        uri.startsWith("/api/products") -> "marketplace-product"
        uri.startsWith("/api/catalog/products") -> "catalog-product"
        uri.startsWith("/api/orders") -> "marketplace-order"
        uri.startsWith("/api/shop-orders") -> "catalog-order"
        else -> null
    }
}
