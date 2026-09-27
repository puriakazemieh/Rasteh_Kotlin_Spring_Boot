package com.kazemieh.rasteh.canonical.domain

import java.math.BigDecimal

/**
 * Transitional adapter for marketplace amounts. Product/finance ownership has
 * approved the legacy marketplace unit as IRR, so this deliberately performs
 * no toman conversion. New canonical APIs expose [Long] minor units only.
 */
object LegacyIrrMoney {
    fun toMinorUnits(amount: BigDecimal): Long {
        require(amount.signum() >= 0) { "Legacy IRR amount must not be negative" }
        return try {
            amount.longValueExact()
        } catch (_: ArithmeticException) {
            throw IllegalArgumentException("Legacy IRR amount must be an exact integer")
        }
    }
}
