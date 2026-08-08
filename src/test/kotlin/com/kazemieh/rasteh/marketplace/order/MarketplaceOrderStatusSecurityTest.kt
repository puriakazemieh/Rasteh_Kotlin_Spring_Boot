package com.kazemieh.rasteh.marketplace.order

import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.marketplace.domain.OrderStatus
import com.kazemieh.rasteh.marketplace.order.entity.MarketplaceOrderEntity
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.shared.error.InvalidOrderStatusException
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.Optional

class MarketplaceOrderStatusSecurityTest {
    private val orders = mockk<MarketplaceOrderRepository>()
    private val service = MarketplaceOrderService(
        orders,
        mockk<ShopRepository>(relaxed = true),
        mockk<ShopProductRepository>(relaxed = true),
        mockk<UserRepository>(relaxed = true)
    )

    @Test
    fun `rejects transition from completed order`() {
        every { orders.findById(9L) } returns Optional.of(MarketplaceOrderEntity(id = 9L, status = OrderStatus.COMPLETED))

        assertThatThrownBy { service.updateStatus(1L, true, 9L, "PREPARING") }
            .isInstanceOf(InvalidOrderStatusException::class.java)
    }
}
