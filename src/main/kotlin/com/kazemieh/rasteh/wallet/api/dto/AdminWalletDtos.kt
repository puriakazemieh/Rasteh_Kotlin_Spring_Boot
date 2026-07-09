package com.kazemieh.rasteh.wallet.api.dto

import java.math.BigDecimal

data class UserWalletInfoResponse(
    val userId: Long,
    val email: String?,
    val fullName: String?,
    val balance: BigDecimal
)
