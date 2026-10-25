package com.kazemieh.rasteh.canonical.application

import com.kazemieh.rasteh.canonical.persistence.CatalogEventRepository
import com.kazemieh.rasteh.canonical.persistence.entity.CatalogEventEntity
import org.springframework.stereotype.Service
import java.util.UUID

/** Durable event log; a downstream analytics sink can consume it without coupling offer writes to a vendor. */
@Service
class CatalogEventService(private val events: CatalogEventRepository) {
    fun record(type: String, offerId: UUID, payload: String = "{}") =
        events.save(CatalogEventEntity(eventType = type, offerId = offerId, payloadJson = payload))
}
