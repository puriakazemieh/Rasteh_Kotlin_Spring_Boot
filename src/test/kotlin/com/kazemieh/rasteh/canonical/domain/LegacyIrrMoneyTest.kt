package com.kazemieh.rasteh.canonical.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class LegacyIrrMoneyTest {
    @Test
    fun `accepts exact legacy IRR without toman conversion`() {
        assertThat(LegacyIrrMoney.toMinorUnits(BigDecimal("125000"))).isEqualTo(125000L)
    }

    @Test
    fun `rejects fractional or negative legacy amounts`() {
        assertThatThrownBy { LegacyIrrMoney.toMinorUnits(BigDecimal("12.50")) }
            .isInstanceOf(IllegalArgumentException::class.java)
        assertThatThrownBy { LegacyIrrMoney.toMinorUnits(BigDecimal("-1")) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
