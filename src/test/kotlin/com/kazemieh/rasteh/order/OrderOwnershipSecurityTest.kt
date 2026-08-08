package com.kazemieh.rasteh.order

import com.fasterxml.jackson.databind.ObjectMapper
import com.kazemieh.rasteh.cart.persistence.CartRepository
import com.kazemieh.rasteh.catalog.persistence.InventoryRepository
import com.kazemieh.rasteh.catalog.persistence.ProductVariantRepository
import com.kazemieh.rasteh.customer.address.persistence.AddressRepository
import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.order.application.OrderService
import com.kazemieh.rasteh.order.application.exception.OrderNotFoundException
import com.kazemieh.rasteh.order.persistence.OrderRepository
import com.kazemieh.rasteh.wallet.application.WalletService
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class OrderOwnershipSecurityTest {
    private val orders = mockk<OrderRepository>()
    private val service = OrderService(
        orders, mockk<UserRepository>(), mockk<AddressRepository>(), mockk<ProductVariantRepository>(),
        mockk<InventoryRepository>(), mockk<CartRepository>(), ObjectMapper(), mockk<WalletService>()
    )

    @Test
    fun `tracking another users order is not disclosed`() {
        every { orders.findByIdAndUserId(42L, 7L) } returns null
        assertThatThrownBy { service.trackMyOrder(7L, 42L) }.isInstanceOf(OrderNotFoundException::class.java)
    }
}
