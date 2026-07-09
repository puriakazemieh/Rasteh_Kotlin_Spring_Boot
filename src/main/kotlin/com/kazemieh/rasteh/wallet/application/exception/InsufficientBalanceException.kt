package com.kazemieh.rasteh.wallet.application.exception

import com.kazemieh.rasteh.shared.error.ApiException
import com.kazemieh.rasteh.shared.error.ErrorCodes
import org.springframework.http.HttpStatus

class InsufficientBalanceException : ApiException(
    message = "Insufficient wallet balance",
    errorCode = ErrorCodes.INSUFFICIENT_WALLET_BALANCE,
    httpStatus = HttpStatus.BAD_REQUEST
)
