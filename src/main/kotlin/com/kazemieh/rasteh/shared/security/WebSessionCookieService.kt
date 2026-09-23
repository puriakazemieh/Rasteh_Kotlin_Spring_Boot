package com.kazemieh.rasteh.shared.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class WebSessionCookieService(
    private val properties: WebSessionProperties,
) {
    fun refreshToken(request: HttpServletRequest): String? =
        request.cookies
            ?.firstOrNull { it.name == properties.refreshCookieName }
            ?.value

    fun accessToken(request: HttpServletRequest): String? =
        request.cookies
            ?.firstOrNull { it.name == properties.accessCookieName }
            ?.value

    fun writeSession(response: HttpServletResponse, accessToken: String, refreshToken: String) {
        addCookie(response, properties.accessCookieName, accessToken, Duration.ofMinutes(15))
        addCookie(response, properties.refreshCookieName, refreshToken, Duration.ofDays(30))
    }

    fun clearSession(response: HttpServletResponse) {
        addCookie(response, properties.accessCookieName, "", Duration.ZERO)
        addCookie(response, properties.refreshCookieName, "", Duration.ZERO)
    }

    private fun addCookie(
        response: HttpServletResponse,
        name: String,
        value: String,
        maxAge: Duration,
    ) {
        val cookie = ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(properties.secure)
            .sameSite(properties.sameSite)
            .path("/")
            .maxAge(maxAge)
            .apply {
                properties.cookieDomain?.takeIf { it.isNotBlank() }?.let(::domain)
            }
            .build()
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }
}
