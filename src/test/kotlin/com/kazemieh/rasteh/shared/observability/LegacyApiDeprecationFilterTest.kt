package com.kazemieh.rasteh.shared.observability

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockFilterChain

class LegacyApiDeprecationFilterTest {
    @Test
    fun `marks legacy marketplace endpoint and records bounded telemetry`() {
        val registry = SimpleMeterRegistry()
        val response = MockHttpServletResponse()

        LegacyApiDeprecationFilter(registry).doFilter(
            MockHttpServletRequest("GET", "/api/products/12"),
            response,
            MockFilterChain(),
        )

        assertThat(response.getHeader("Deprecation")).isEqualTo("true")
        assertThat(response.getHeader("Link")).contains("/api/v1/offers")
        assertThat(registry.find("rasteh.legacy_api.request.total").counter()?.count()).isEqualTo(1.0)
    }
}
