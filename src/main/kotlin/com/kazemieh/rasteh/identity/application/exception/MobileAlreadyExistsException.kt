package com.kazemieh.rasteh.identity.application.exception

import com.kazemieh.rasteh.shared.error.ConflictException

class MobileAlreadyExistsException(mobile: String)
    : ConflictException("Mobile already exists: $mobile", IdentityErrorCodes.MOBILE_ALREADY_EXISTS)
