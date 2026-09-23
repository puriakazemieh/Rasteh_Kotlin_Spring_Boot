package com.kazemieh.rasteh.shared.security

import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletResponse
import org.assertj.core.api.Assertions.assertThat

class WebSessionCookieServiceTest {
    private val service = WebSessionCookieService(
        WebSessionProperties(
            secure = true,
            sameSite = "Strict",
            cookieDomain = "staging.example.invalid",
        ),
    )

    @Test
    fun `session cookies are HttpOnly secure and same-site`() {
        val response = MockHttpServletResponse()

        service.writeSession(response, "test-access", "test-refresh")

        val cookies = response.getHeaders("Set-Cookie")
        assertThat(cookies).anyMatch { it.startsWith("RASTEH_WEB_ACCESS=") && "HttpOnly" in it && "Secure" in it && "SameSite=Strict" in it && "Domain=staging.example.invalid" in it }
        assertThat(cookies).anyMatch { it.startsWith("RASTEH_WEB_REFRESH=") && "HttpOnly" in it && "Secure" in it && "SameSite=Strict" in it && "Domain=staging.example.invalid" in it }
    }

    @Test
    fun `clearing a session expires both cookies`() {
        val response = MockHttpServletResponse()

        service.clearSession(response)

        val cookies = response.getHeaders("Set-Cookie")
        assertThat(cookies).anyMatch { it.startsWith("RASTEH_WEB_ACCESS=") && "Max-Age=0" in it }
        assertThat(cookies).anyMatch { it.startsWith("RASTEH_WEB_REFRESH=") && "Max-Age=0" in it }
    }
}
