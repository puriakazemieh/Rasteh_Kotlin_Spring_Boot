package com.kazemieh.rasteh.identity.application.exception

import com.kazemieh.rasteh.shared.error.UnauthorizedException

class InvalidOtpException(message: String = "Invalid or expired OTP")
    : UnauthorizedException(message, IdentityErrorCodes.INVALID_OTP)
