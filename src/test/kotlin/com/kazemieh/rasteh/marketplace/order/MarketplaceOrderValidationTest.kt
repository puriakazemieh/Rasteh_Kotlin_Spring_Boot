package com.kazemieh.rasteh.marketplace.order

import jakarta.validation.Validation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MarketplaceOrderValidationTest {
    private val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `rejects zero and negative marketplace quantities including nested request items`() {
        val zero = CreateOrderRequest(shopId = 1L, items = listOf(OrderItemRequest(productId = 1L, quantity = 0)))
        val negative = CreateOrderRequest(shopId = 1L, items = listOf(OrderItemRequest(productId = 1L, quantity = -1)))

        assertThat(validator.validate(zero)).isNotEmpty
        assertThat(validator.validate(negative)).isNotEmpty
    }
}
