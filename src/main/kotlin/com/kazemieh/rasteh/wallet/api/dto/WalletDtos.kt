package com.kazemieh.rasteh.wallet.api.dto

import com.kazemieh.rasteh.wallet.persistence.entity.TransactionType
import com.kazemieh.rasteh.wallet.persistence.entity.WithdrawalStatus
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal
import java.time.OffsetDateTime

data class WalletResponse(
    val balance: BigDecimal,
    val userId: Long
)

data class WalletTransactionResponse(
    val id: Long,
    val amount: BigDecimal,
    val type: TransactionType,
    val description: String?,
    val referenceId: String?,
    val createdAt: OffsetDateTime?
)

data class TopUpRequest(
    @field:DecimalMin(value = "0.01", inclusive = true)
    val amount: BigDecimal
)

data class WithdrawalRequest(
    @field:DecimalMin(value = "0.01", inclusive = true)
    val amount: BigDecimal,
    @field:NotBlank
    val iban: String
)

data class WithdrawalRequestResponse(
    val id: Long,
    val userId: Long,
    val userFullName: String?,
    val userEmail: String?,
    val amount: BigDecimal,
    val iban: String,
    val status: WithdrawalStatus,
    val adminNote: String?,
    val createdAt: OffsetDateTime?
)

data class AdminAdjustBalanceRequest(
    val userId: Long,
    val amount: BigDecimal,
    val description: String? = null
)

data class AdminProcessWithdrawalRequest(
    val status: WithdrawalStatus,
    val adminNote: String? = null
)
