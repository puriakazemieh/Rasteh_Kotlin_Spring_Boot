package com.kazemieh.rasteh.shared.migration

import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.springframework.core.io.ClassPathResource
import java.security.SecureRandom
import java.util.Base64
import java.sql.DriverManager

@Testcontainers
@SpringBootTest(properties = [
    "spring.jpa.hibernate.ddl-auto=validate",
    "app.seed.enabled=false",
    "spring.flyway.baseline-on-migrate=false",
])
class FlywayMigrationTest @Autowired constructor(
    private val flyway: Flyway,
) {
    @Test
    fun `an empty PostgreSQL database reaches the complete local migration version`() {
        val migrations = flyway.info().applied().mapNotNull { it.version?.version }

        assertThat(migrations).containsExactly("0", "1", "2", "3", "4")
    }

    @Test
    fun `a local baseline snapshot is upgraded without replaying its baseline`() {
        DriverManager.getConnection(legacyDatabaseUrl(), postgres.username, postgres.password).use { connection ->
            val baselineSql = ClassPathResource("db/migration/V0__local_schema_baseline.sql")
                .inputStream.bufferedReader().use { it.readText() }
            connection.createStatement().use { statement ->
                baselineSql.splitToSequence(";")
                    .map(String::trim)
                    .filter(String::isNotBlank)
                    .forEach(statement::execute)
            }
        }

        val migrated = Flyway.configure()
            .dataSource(legacyDatabaseUrl(), postgres.username, postgres.password)
            .locations("classpath:db/migration")
            .baselineOnMigrate(true)
            .baselineVersion("0")
            .load()
            .also { it.migrate() }

        assertThat(migrated.info().applied().mapNotNull { it.version?.version })
            .containsExactly("0", "1", "2", "3", "4")
    }

    private fun legacyDatabaseUrl(): String =
        "jdbc:postgresql://${postgres.host}:${postgres.getMappedPort(5432)}/postgres"

    companion object {
        @Container
        @JvmStatic
        val postgres = LocalPostgresContainer()

        private val jwtSecret = ByteArray(48).also(SecureRandom()::nextBytes)
            .let(Base64.getEncoder()::encodeToString)

        @DynamicPropertySource
        @JvmStatic
        fun databaseProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("jwt.secret-key") { jwtSecret }
        }
    }

    class LocalPostgresContainer : PostgreSQLContainer<LocalPostgresContainer>("postgres:16-alpine")
}
