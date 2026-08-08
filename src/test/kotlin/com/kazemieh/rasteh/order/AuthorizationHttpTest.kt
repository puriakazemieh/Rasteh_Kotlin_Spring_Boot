package com.kazemieh.rasteh.order

import com.fasterxml.jackson.databind.ObjectMapper
import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.identity.persistence.entity.UserEntity
import com.kazemieh.rasteh.order.persistence.OrderRepository
import com.kazemieh.rasteh.order.persistence.entity.OrderEntity
import com.kazemieh.rasteh.shared.security.UserPrincipal
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.security.SecureRandom
import java.util.Base64

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = [
    "spring.datasource.url=jdbc:h2:mem:authorization-http;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false",
    "app.seed.enabled=false",
])
@ContextConfiguration(initializers = [AuthorizationHttpTest.RandomJwtTestProperty::class])
class AuthorizationHttpTest @Autowired constructor(
    private val context: WebApplicationContext,
    private val users: UserRepository,
    private val orders: OrderRepository,
    private val objectMapper: ObjectMapper,
) {
    private lateinit var mockMvc: MockMvc

    class RandomJwtTestProperty : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(context: ConfigurableApplicationContext) {
            val bytes = ByteArray(48).also(SecureRandom()::nextBytes)
            TestPropertyValues.of("jwt.secret-key=${Base64.getEncoder().encodeToString(bytes)}").applyTo(context)
        }
    }

    @BeforeEach
    fun setUp() {
        val builder: DefaultMockMvcBuilder = MockMvcBuilders.webAppContextSetup(context)
        builder.apply<DefaultMockMvcBuilder>(springSecurity())
        mockMvc = builder.build()
    }

    @Test
    fun `user role is forbidden from every admin route`() {
        mockMvc.perform(get("/api/admin/stats").with(user(userPrincipal(11L))))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `user cannot read another users order over HTTP`() {
        val owner = users.save(UserEntity(email = "owner@example.invalid", passwordHash = "test"))
        val order = orders.save(
            OrderEntity(
                user = owner,
                addressSnapshot = objectMapper.createObjectNode(),
            )
        )

        mockMvc.perform(get("/api/shop-orders/${order.id}").with(user(userPrincipal(12L))))
            .andExpect(status().isNotFound)
    }

    private fun userPrincipal(id: Long) = UserPrincipal(id, "test-user-$id", "not-used", "USER", true)
}
