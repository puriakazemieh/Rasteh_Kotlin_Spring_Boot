package com.kazemieh.rasteh.marketplace.order

import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.identity.persistence.entity.UserEntity
import com.kazemieh.rasteh.marketplace.domain.ShopStatus
import com.kazemieh.rasteh.marketplace.domain.ShopType
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopEntity
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopProductEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import java.math.BigDecimal
import java.security.SecureRandom
import java.util.Base64
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = [
    "spring.datasource.url=jdbc:h2:mem:marketplace-oversell;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false",
    "app.seed.enabled=false",
])
@ContextConfiguration(initializers = [MarketplaceOversellConcurrencyTest.RandomJwtTestProperty::class])
class MarketplaceOversellConcurrencyTest @Autowired constructor(
    private val service: MarketplaceOrderService,
    private val users: UserRepository,
    private val shops: ShopRepository,
    private val products: ShopProductRepository,
    private val orders: MarketplaceOrderRepository,
) {
    class RandomJwtTestProperty : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(context: ConfigurableApplicationContext) {
            val bytes = ByteArray(48).also(SecureRandom()::nextBytes)
            TestPropertyValues.of("jwt.secret-key=${Base64.getEncoder().encodeToString(bytes)}").applyTo(context)
        }
    }

    @Test
    fun `two concurrent orders cannot oversell the final unit`() {
        val owner = users.save(UserEntity(email = "shop-owner@example.invalid", passwordHash = "test"))
        val customerOne = users.save(UserEntity(email = "customer-one@example.invalid", passwordHash = "test"))
        val customerTwo = users.save(UserEntity(email = "customer-two@example.invalid", passwordHash = "test"))
        val shop = shops.save(ShopEntity(owner = owner, name = "Test shop", type = ShopType.BUYABLE, status = ShopStatus.APPROVED))
        val product = products.save(ShopProductEntity(shop = shop, name = "Final unit", price = BigDecimal("1000"), stock = 1))

        val ready = CountDownLatch(2)
        val start = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(2)
        try {
            val attempts = listOf(customerOne, customerTwo).map { customer ->
                executor.submit<Boolean> {
                    ready.countDown()
                    check(start.await(10, TimeUnit.SECONDS))
                    runCatching {
                        service.create(
                            customer.id,
                            CreateOrderRequest(shopId = shop.id, items = listOf(OrderItemRequest(productId = product.id, quantity = 1))),
                        )
                    }.isSuccess
                }
            }
            check(ready.await(10, TimeUnit.SECONDS))
            start.countDown()

            assertThat(attempts.count { it.get(15, TimeUnit.SECONDS) }).isEqualTo(1)
            assertThat(products.findById(product.id).orElseThrow().stock).isZero()
            assertThat(orders.count()).isEqualTo(1)
        } finally {
            executor.shutdownNow()
        }
    }
}
