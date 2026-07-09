package com.kazemieh.rasteh.shared.config

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class DatabaseMigrationConfig {

    @Bean
    fun migrateDatabase(jdbcTemplate: JdbcTemplate) = CommandLineRunner {
        // هر عبارت در try/catch جدا تا خطای یکی مانعِ اجرای بقیه نشود.
        safe(jdbcTemplate, "ALTER TABLE users ALTER COLUMN email DROP NOT NULL;")
        safe(jdbcTemplate, "ALTER TABLE users ALTER COLUMN phone DROP NOT NULL;")
        // Security: registered users must default to CUSTOMER, never ADMIN/VENDOR.
        safe(jdbcTemplate, "ALTER TABLE users ALTER COLUMN role SET DEFAULT 'CUSTOMER';")
    }

    private fun safe(jdbcTemplate: JdbcTemplate, sql: String) {
        try {
            jdbcTemplate.execute(sql)
        } catch (e: Exception) {
            // Ignore if it fails (e.g. column already nullable or table doesn't exist yet)
            println("Note: migration statement skipped: ${e.message}")
        }
    }
}
