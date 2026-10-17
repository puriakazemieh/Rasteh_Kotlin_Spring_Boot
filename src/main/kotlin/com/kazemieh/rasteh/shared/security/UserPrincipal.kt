package com.kazemieh.rasteh.shared.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal(
    val id: Long,
    private val username: String,
    private val passwordHash: String,
    private val roleNames: Set<String>,
    private val active: Boolean
) : UserDetails {
    constructor(id: Long, username: String, passwordHash: String, roleName: String, active: Boolean) :
        this(id, username, passwordHash, setOf(roleName), active)

    override fun getAuthorities(): Collection<GrantedAuthority> =
        roleNames.map { SimpleGrantedAuthority("ROLE_$it") }

    override fun getPassword(): String = passwordHash
    override fun getUsername(): String = username

    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = active
}
