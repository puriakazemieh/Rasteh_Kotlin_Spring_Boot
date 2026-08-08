package com.kazemieh.rasteh.wallet

import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.shared.error.BadRequestException
import com.kazemieh.rasteh.wallet.api.dto.WithdrawalRequest
import com.kazemieh.rasteh.wallet.application.WalletService
import com.kazemieh.rasteh.wallet.persistence.WalletRepository
import com.kazemieh.rasteh.wallet.persistence.WalletTransactionRepository
import com.kazemieh.rasteh.wallet.persistence.WithdrawalRequestRepository
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class WalletServiceSecurityTest {
    private val service = WalletService(
        mockk<WalletRepository>(relaxed = true),
        mockk<WalletTransactionRepository>(relaxed = true),
        mockk<WithdrawalRequestRepository>(relaxed = true),
        mockk<UserRepository>(relaxed = true),
        true
    )

    @Test
    fun `negative withdrawal is rejected before it can mutate a balance`() {
        assertThatThrownBy {
            service.requestWithdrawal(1L, WithdrawalRequest(BigDecimal("-1.00"), "IR000000000000000000000000"))
        }.isInstanceOf(BadRequestException::class.java)
    }
}
