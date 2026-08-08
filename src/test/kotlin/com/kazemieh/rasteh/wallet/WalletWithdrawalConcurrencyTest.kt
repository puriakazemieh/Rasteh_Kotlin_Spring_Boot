package com.kazemieh.rasteh.wallet

import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.identity.persistence.entity.UserEntity
import com.kazemieh.rasteh.wallet.api.dto.WithdrawalRequest
import com.kazemieh.rasteh.wallet.application.WalletService
import com.kazemieh.rasteh.wallet.persistence.WalletRepository
import com.kazemieh.rasteh.wallet.persistence.WithdrawalRequestRepository
import com.kazemieh.rasteh.wallet.persistence.entity.WalletEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.ApplicationContextInitializer
import org.springframework.test.context.ContextConfiguration
import org.springframework.boot.test.util.TestPropertyValues
import java.math.BigDecimal
import java.security.SecureRandom
import java.util.Base64
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = [
    "spring.datasource.url=jdbc:h2:mem:wallet-concurrency;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false",
    "app.wallet.enabled=true",
    "app.seed.enabled=false",
])
@ContextConfiguration(initializers = [WalletWithdrawalConcurrencyTest.RandomJwtTestProperty::class])
class WalletWithdrawalConcurrencyTest @Autowired constructor(
    private val walletService: WalletService,
    private val userRepository: UserRepository,
    private val walletRepository: WalletRepository,
    private val withdrawalRepository: WithdrawalRequestRepository,
) {
    class RandomJwtTestProperty : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(context: ConfigurableApplicationContext) {
            val bytes = ByteArray(48).also(SecureRandom()::nextBytes)
            TestPropertyValues.of("jwt.secret-key=${Base64.getEncoder().encodeToString(bytes)}").applyTo(context)
        }
    }

    @Test
    fun `two concurrent withdrawals cannot overdraw one wallet`() {
        val user = userRepository.save(UserEntity(email = "wallet-concurrency@example.invalid", passwordHash = "test"))
        walletRepository.save(WalletEntity(user = user, balance = BigDecimal("100")))

        val ready = CountDownLatch(2)
        val start = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(2)
        try {
            val attempts = List(2) {
                executor.submit<Boolean> {
                    ready.countDown()
                    check(start.await(10, TimeUnit.SECONDS))
                    runCatching {
                        walletService.requestWithdrawal(
                            user.id,
                            WithdrawalRequest(BigDecimal("80"), "IR000000000000000000000000"),
                        )
                    }.isSuccess
                }
            }
            check(ready.await(10, TimeUnit.SECONDS))
            start.countDown()

            assertThat(attempts.count { it.get(15, TimeUnit.SECONDS) }).isEqualTo(1)
            assertThat(walletRepository.findByUserId(user.id)?.balance).isEqualByComparingTo("20")
            assertThat(withdrawalRepository.count()).isEqualTo(1)
        } finally {
            executor.shutdownNow()
        }
    }
}
