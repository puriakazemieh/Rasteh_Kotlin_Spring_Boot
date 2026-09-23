package com.kazemieh.rasteh.shared.observability

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.micrometer.observation.ObservationRegistry
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import java.util.UUID

class RequestCorrelationFilterTest {
    private val meterRegistry = SimpleMeterRegistry()
    private val filter = RequestCorrelationFilter(meterRegistry, ObservationRegistry.create())

    @Test
    fun `preserves a valid client request id`() {
        val requestId = UUID.randomUUID().toString()
        val request = MockHttpServletRequest().apply { addHeader("X-Request-Id", requestId) }
        val response = MockHttpServletResponse()

        filter.doFilter(request, response, MockFilterChain())

        assertThat(response.getHeader("X-Request-Id")).isEqualTo(requestId)
    }

    @Test
    fun `replaces an unsafe client request id`() {
        val request = MockHttpServletRequest().apply { addHeader("X-Request-Id", "invalid\r\nvalue") }
        val response = MockHttpServletResponse()

        filter.doFilter(request, response, MockFilterChain())

        assertThat(response.getHeader("X-Request-Id")).matches { candidate ->
            runCatching { UUID.fromString(candidate) }.isSuccess
        }
    }

    @Test
    fun `records bounded critical operation metrics for protected domains`() {
        listOf(
            "/api/auth/login" to "auth",
            "/api/orders" to "order",
            "/api/payments/start" to "payment",
            "/api/admin/inventory" to "inventory",
        ).forEach { (uri, operation) ->
            filter.doFilter(MockHttpServletRequest("POST", uri), MockHttpServletResponse(), MockFilterChain())

            assertThat(
                meterRegistry.find("rasteh.critical.request.total")
                    .tag("operation", operation)
                    .counter()
                    ?.count(),
            ).isEqualTo(1.0)
        }
    }
}
