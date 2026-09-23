package com.kazemieh.rasteh.identity.api

import com.kazemieh.rasteh.identity.api.dto.AuthResponse
import com.kazemieh.rasteh.identity.api.dto.LoginRequest
import com.kazemieh.rasteh.identity.api.dto.RegisterRequest
import com.kazemieh.rasteh.identity.api.dto.UserResponse
import com.kazemieh.rasteh.identity.application.AuthService
import com.kazemieh.rasteh.identity.application.UserMeService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import com.kazemieh.rasteh.shared.security.WebSessionCookieService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletResponse
import java.time.OffsetDateTime

class WebSessionControllerTest {
    private val authService = mockk<AuthService>()
    private val userMeService = mockk<UserMeService>()
    private val cookieService = mockk<WebSessionCookieService>(relaxed = true)
    private val controller = WebSessionController(authService, userMeService, cookieService)

    @Test
    fun `login writes server-side session cookies and returns only user data`() {
        val request = LoginRequest(username = "user@example.test", password = "test-password")
        val user = UserResponse(1, null, null, null, null, null, null, "USER", true, OffsetDateTime.now(), OffsetDateTime.now())
        every { authService.login(request) } returns AuthResponse("test-access", "test-refresh", user)

        val result = controller.login(request, MockHttpServletResponse())

        assertThat(result).isEqualTo(user)
        verify(exactly = 1) { cookieService.writeSession(any(), "test-access", "test-refresh") }
    }

    @Test
    fun `register writes server-side session cookies and returns only user data`() {
        val request = RegisterRequest(email = "new-user@example.test", password = "test-password")
        val user = UserResponse(2, "new-user@example.test", null, null, null, null, null, "USER", true, OffsetDateTime.now(), OffsetDateTime.now())
        every { authService.register(request) } returns AuthResponse("test-access", "test-refresh", user)

        val result = controller.register(request, MockHttpServletResponse())

        assertThat(result).isEqualTo(user)
        verify(exactly = 1) { cookieService.writeSession(any(), "test-access", "test-refresh") }
    }

    @Test
    fun `session returns the authenticated cookie user's data`() {
        val principal = UserPrincipal(3, "user@example.test", "hash", "USER", true)
        val user = UserResponse(3, "user@example.test", null, null, null, null, null, "USER", true, OffsetDateTime.now(), OffsetDateTime.now())
        every { userMeService.getMe(3) } returns user

        assertThat(controller.session(principal)).isEqualTo(user)
    }
}
