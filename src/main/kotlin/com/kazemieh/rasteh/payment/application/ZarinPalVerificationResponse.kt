package com.kazemieh.rasteh.payment.application

data class ZarinPalVerificationResponse(
    val isSuccess: Boolean,
    val refId: String? = null
)