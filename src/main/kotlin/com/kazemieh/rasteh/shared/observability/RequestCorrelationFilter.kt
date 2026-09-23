package com.kazemieh.rasteh.shared.observability

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import io.micrometer.common.KeyValue
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

/**
 * Carries a safe correlation id through request logging and returns it to the
 * caller. Client input is accepted only when it is a canonical UUID, avoiding
 * log injection and unbounded-cardinality metric labels.
 */
@Component
class RequestCorrelationFilter(
    private val meterRegistry: MeterRegistry,
    private val observationRegistry: ObservationRegistry,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val requestId = request.getHeader(REQUEST_ID_HEADER)
            ?.takeIf(::isCanonicalUuid)
            ?: UUID.randomUUID().toString()
        val operation = criticalOperationFor(request.requestURI)
        val observation = Observation.start("rasteh.critical.request", observationRegistry)
            .lowCardinalityKeyValue(KeyValue.of("operation", operation))

        response.setHeader(REQUEST_ID_HEADER, requestId)
        MDC.put(REQUEST_ID_MDC_KEY, requestId)
        try {
            meterRegistry.counter(CRITICAL_REQUEST_METRIC, "operation", operation).increment()
            filterChain.doFilter(request, response)
        } finally {
            observation.stop()
            MDC.remove(REQUEST_ID_MDC_KEY)
        }
    }

    private fun criticalOperationFor(uri: String): String = when {
        uri.startsWith("/api/auth") -> "auth"
        uri.startsWith("/api/orders") -> "order"
        uri.startsWith("/api/payments") -> "payment"
        uri.startsWith("/api/admin/inventory") -> "inventory"
        else -> "other"
    }

    private fun isCanonicalUuid(value: String): Boolean =
        runCatching { UUID.fromString(value).toString() == value.lowercase() }.getOrDefault(false)

    private companion object {
        const val REQUEST_ID_HEADER = "X-Request-Id"
        const val REQUEST_ID_MDC_KEY = "requestId"
        const val CRITICAL_REQUEST_METRIC = "rasteh.critical.request.total"
    }
}
