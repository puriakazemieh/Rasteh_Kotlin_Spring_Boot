package com.kazemieh.rasteh.engagement

import com.kazemieh.rasteh.engagement.entity.ParkingEntity
import com.kazemieh.rasteh.engagement.entity.ReferralEntity
import com.kazemieh.rasteh.shared.error.BadRequestException
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Optional

class ReferralServiceTest {
    private val repo = mockk<ReferralRepository>(relaxed = true)
    private val service = ReferralService(repo)

    @Test
    fun `myReferral creates a code when the user has none`() {
        every { repo.findFirstByInviterUserIdAndInviteeUserIdIsNull(1L) } returns null
        every { repo.findFirstByCode(any()) } returns null
        every { repo.save(any()) } answers { firstArg() }
        every { repo.countByInviterUserIdAndInviteeUserIdIsNotNull(1L) } returns 0L

        val res = service.myReferral(1L)

        assertThat(res.code).isNotBlank()
        assertThat(res.invitedCount).isEqualTo(0L)
        assertThat(res.rewardStatus).isEqualTo("PENDING")
    }

    @Test
    fun `redeem rejects using your own code`() {
        every { repo.findFirstByCodeAndInviteeUserIdIsNull("ABC123") } returns
            ReferralEntity(inviterUserId = 5L, code = "ABC123")

        assertThatThrownBy { service.redeem(5L, RedeemReferralRequest("ABC123")) }
            .isInstanceOf(BadRequestException::class.java)
    }
}

class ParkingServiceTest {
    private val repo = mockk<ParkingRepository>(relaxed = true)
    private val service = ParkingService(repo)

    @Test
    fun `pay computes a fee, marks paid and sets exit time`() {
        val entity = ParkingEntity(id = 3L, userId = 7L, spot = "B-12", enteredAt = OffsetDateTime.now().minusHours(2))
        every { repo.findById(3L) } returns Optional.of(entity)

        val res = service.pay(7L, 3L)

        assertThat(res.paid).isTrue()
        assertThat(res.fee).isGreaterThan(BigDecimal.ZERO)
    }

    @Test
    fun `pay rejects another user's session`() {
        val entity = ParkingEntity(id = 3L, userId = 7L, spot = "B-12", enteredAt = OffsetDateTime.now())
        every { repo.findById(3L) } returns Optional.of(entity)

        assertThatThrownBy { service.pay(999L, 3L) }
            .isInstanceOf(BadRequestException::class.java)
    }
}
