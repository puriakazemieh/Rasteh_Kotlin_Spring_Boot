package com.kazemieh.rasteh.shared.observability

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.security.SecureRandom
import java.util.Base64
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = [
    "spring.datasource.url=jdbc:h2:mem:observability-http;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false",
    "app.seed.enabled=false",
])
@ContextConfiguration(initializers = [ObservabilityHttpTest.RandomJwtTestProperty::class])
class ObservabilityHttpTest @Autowired constructor(
    context: WebApplicationContext,
) {
    private val mockMvc: MockMvc = MockMvcBuilders.webAppContextSetup(context).let { builder ->
        builder.apply<DefaultMockMvcBuilder>(springSecurity())
        builder.build()
    }

    @Test
    fun `health endpoints are public and preserve correlation ids`() {
        val requestId = UUID.randomUUID().toString()

        mockMvc.perform(get("/actuator/health/readiness").header("X-Request-Id", requestId))
            .andExpect(status().isOk)
            .andExpect(header().string("X-Request-Id", requestId))
    }

    class RandomJwtTestProperty : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(context: ConfigurableApplicationContext) {
            val bytes = ByteArray(48).also(SecureRandom()::nextBytes)
            TestPropertyValues.of("jwt.secret-key=${Base64.getEncoder().encodeToString(bytes)}").applyTo(context)
        }
    }
}
