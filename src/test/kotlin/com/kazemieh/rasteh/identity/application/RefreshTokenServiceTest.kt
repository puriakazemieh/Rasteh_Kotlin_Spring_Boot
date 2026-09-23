package com.kazemieh.rasteh.identity.application

import com.kazemieh.rasteh.identity.application.exception.InvalidCredentialsException
import com.kazemieh.rasteh.identity.persistence.RefreshTokenRepository
import com.kazemieh.rasteh.identity.persistence.entity.RefreshTokenEntity
import com.kazemieh.rasteh.identity.persistence.entity.UserEntity
import com.kazemieh.rasteh.shared.security.jwt.JwtProperties
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class RefreshTokenServiceTest {
    private val repository = mockk<RefreshTokenRepository>(relaxed = true)
    private val service = RefreshTokenService(
        repository,
        JwtProperties("test-secret-key-material-that-is-long-enough", "test", 15, 30),
    )

    @Test
    fun `replayed revoked refresh token revokes every active session for its owner`() {
        val user = UserEntity(id = 7)
        val token = RefreshTokenEntity(
            user = user,
            tokenHash = "hash",
            expiresAt = OffsetDateTime.now().plusDays(1),
            revokedAt = OffsetDateTime.now().minusMinutes(1),
        )
        every { repository.findByTokenHash(any()) } returns token

        assertThatThrownBy { service.rotate("replayed-token") }
            .isInstanceOf(InvalidCredentialsException::class.java)

        verify(exactly = 1) { repository.revokeAllActiveForUser(7, any()) }
    }
}
