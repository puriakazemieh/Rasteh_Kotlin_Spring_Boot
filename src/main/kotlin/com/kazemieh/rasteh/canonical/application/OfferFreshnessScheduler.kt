package com.kazemieh.rasteh.canonical.application

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OfferFreshnessScheduler(
    private val offers: OfferWriteService,
    private val inventory: InventoryService,
    private val lock: CatalogSchedulerLock,
) {
    @Scheduled(fixedDelayString = "\${app.catalog.freshness-sweep-ms:60000}")
    fun expireStaleOffers() = lock.withLock {
        offers.expire()
        inventory.expireReservations()
    }
}
