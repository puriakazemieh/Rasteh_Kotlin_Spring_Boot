package com.kazemieh.rasteh.identity.persistence

import com.kazemieh.rasteh.identity.domain.UserRole
import com.kazemieh.rasteh.identity.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun countByRole(role: UserRole): Long
    fun findByEmail(email: String): UserEntity?
    fun findByPhone(phone: String): UserEntity?
    fun findByEmailOrPhone(email: String, phone: String): UserEntity?
    fun existsByEmail(email: String): Boolean
    fun existsByPhone(phone: String): Boolean
    fun findByResetPasswordToken(token: String): Optional<UserEntity>
}
