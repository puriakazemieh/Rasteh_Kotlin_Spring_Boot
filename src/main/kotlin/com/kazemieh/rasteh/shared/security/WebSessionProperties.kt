package com.kazemieh.rasteh.shared.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.web-session")
data class WebSessionProperties(
    val accessCookieName: String = "RASTEH_WEB_ACCESS",
    val refreshCookieName: String = "RASTEH_WEB_REFRESH",
    val cookieDomain: String? = null,
    val secure: Boolean = false,
    val sameSite: String = "Strict",
)
