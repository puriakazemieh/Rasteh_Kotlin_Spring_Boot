package com.kazemieh.rasteh.canonical.application

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

/** PostgreSQL advisory lock so only one node runs catalogue expiry work at a time. */
@Component
class CatalogSchedulerLock(private val jdbc: JdbcTemplate) {
    private val lockId = 7_001_001L

    fun <T> withLock(action: () -> T): T? {
        val acquired = jdbc.queryForObject("select pg_try_advisory_lock(?)", Boolean::class.java, lockId) ?: false
        if (!acquired) return null
        return try { action() } finally { jdbc.queryForObject("select pg_advisory_unlock(?)", Boolean::class.java, lockId) }
    }
}
