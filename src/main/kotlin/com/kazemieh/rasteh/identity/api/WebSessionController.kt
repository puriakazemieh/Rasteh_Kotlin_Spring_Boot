package com.kazemieh.rasteh.identity.api

import com.kazemieh.rasteh.identity.api.dto.LoginRequest
import com.kazemieh.rasteh.identity.api.dto.LoginWithOtpRequest
import com.kazemieh.rasteh.identity.api.dto.LogoutRequest
import com.kazemieh.rasteh.identity.api.dto.RefreshRequest
import com.kazemieh.rasteh.identity.api.dto.RegisterRequest
import com.kazemieh.rasteh.identity.api.dto.UserResponse
import com.kazemieh.rasteh.identity.application.AuthService
import com.kazemieh.rasteh.identity.application.UserMeService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import com.kazemieh.rasteh.shared.security.WebSessionCookieService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

/**
 * Web-only authentication façade. Browser JavaScript receives user data only;
 * access and refresh credentials are written to HttpOnly cookies.
 */
@RestController
@RequestMapping("/web/auth")
class WebSessionController(
    private val authService: AuthService,
    private val userMeService: UserMeService,
    private val cookieService: WebSessionCookieService,
) {
    @GetMapping("/csrf")
    fun csrf(token: CsrfToken): CsrfToken = token

    @GetMapping("/session")
    fun session(@AuthenticationPrincipal principal: UserPrincipal?): UserResponse {
        val authenticated = principal ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        return userMeService.getMe(authenticated.id)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest, response: HttpServletResponse): UserResponse =
        authService.login(request).also { session ->
            cookieService.writeSession(response, session.accessToken, session.refreshToken)
        }.user

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest, response: HttpServletResponse): UserResponse =
        authService.register(request).also { session ->
            cookieService.writeSession(response, session.accessToken, session.refreshToken)
        }.user

    @PostMapping("/login-with-otp")
    fun loginWithOtp(@Valid @RequestBody request: LoginWithOtpRequest, response: HttpServletResponse): UserResponse =
        authService.loginWithOtp(request).also { session ->
            cookieService.writeSession(response, session.accessToken, session.refreshToken)
        }.user

    @PostMapping("/refresh")
    fun refresh(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): UserResponse {
        val token = cookieService.refreshToken(request) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        return authService.refresh(RefreshRequest(token)).also { session ->
            cookieService.writeSession(response, session.accessToken, session.refreshToken)
        }.user
    }

    @PostMapping("/logout")
    fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        cookieService.refreshToken(request)?.let { authService.logout(LogoutRequest(it)) }
        cookieService.clearSession(response)
    }
}
