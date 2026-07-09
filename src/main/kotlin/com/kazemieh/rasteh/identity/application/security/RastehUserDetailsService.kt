package com.kazemieh.rasteh.identity.application.security

import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.shared.security.UserPrincipal
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class RastehUserDetailsService(
    private val userRepository: UserRepository,
) : UserDetailsService {

    override fun loadUserByUsername(username: String) =
        userRepository.findByEmailOrPhone(username, username)?.let { u ->
            UserPrincipal(
                id = u.id,
                username = u.email ?: u.phone ?: "",
                passwordHash = u.passwordHash,
                roleName = u.role.name,
                active = u.isActive
            )
        } ?: throw UsernameNotFoundException("User not found")
}