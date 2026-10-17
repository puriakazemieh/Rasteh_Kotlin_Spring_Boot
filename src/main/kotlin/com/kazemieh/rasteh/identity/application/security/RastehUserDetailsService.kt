package com.kazemieh.rasteh.identity.application.security

import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.identity.membership.persistence.PlatformRoleGrantRepository
import com.kazemieh.rasteh.shared.security.UserPrincipal
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class RastehUserDetailsService(
    private val userRepository: UserRepository,
    private val grants: PlatformRoleGrantRepository,
) : UserDetailsService {

    override fun loadUserByUsername(username: String) =
        userRepository.findByEmailOrPhone(username, username)?.let { u ->
            UserPrincipal(
                id = u.id,
                username = u.email ?: u.phone ?: "",
                passwordHash = u.passwordHash,
                // Authority is built only from active grants.  Legacy users.role is migration input,
                // never an authorization fallback, so a revoke takes effect on the next request.
                roleNames = grants.findAllByUserIdAndActiveTrue(u.id).map { it.role.name }.toSet(),
                active = u.isActive
            )
        } ?: throw UsernameNotFoundException("User not found")
}
