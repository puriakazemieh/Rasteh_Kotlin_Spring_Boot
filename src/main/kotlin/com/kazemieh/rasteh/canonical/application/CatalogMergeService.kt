package com.kazemieh.rasteh.canonical.application

import com.kazemieh.rasteh.canonical.persistence.CatalogMergeQueueRepository
import com.kazemieh.rasteh.canonical.persistence.entity.CatalogMergeQueueEntity
import com.kazemieh.rasteh.canonical.persistence.entity.CatalogMergeStatus
import com.kazemieh.rasteh.identity.membership.persistence.AuthorizationAuditRepository
import com.kazemieh.rasteh.identity.membership.persistence.entity.AuthorizationAuditEntity
import com.kazemieh.rasteh.shared.error.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CatalogMergeService(
    private val queue: CatalogMergeQueueRepository,
    private val audit: AuthorizationAuditRepository,
) {
    @Transactional
    fun request(actorId: Long, sourceId: UUID, targetId: UUID): CatalogMergeQueueEntity {
        require(sourceId != targetId) { "A product cannot be merged into itself" }
        return queue.save(CatalogMergeQueueEntity(sourceProductId = sourceId, targetProductId = targetId, createdByUserId = actorId)).also {
            log(actorId, "CATALOG_MERGE_REQUESTED", it.id)
        }
    }

    fun pending(): List<CatalogMergeQueueEntity> = queue.findAllByStatusOrderByCreatedAtAsc(CatalogMergeStatus.PENDING)

    @Transactional
    fun review(actorId: Long, id: UUID, approve: Boolean): CatalogMergeQueueEntity {
        val item = queue.findById(id).orElseThrow { NotFoundException("Merge request not found", "CATALOG_MERGE_NOT_FOUND") }
        require(item.status == CatalogMergeStatus.PENDING) { "Merge request is already reviewed" }
        item.status = if (approve) CatalogMergeStatus.APPROVED else CatalogMergeStatus.REJECTED
        item.reviewedByUserId = actorId
        return queue.save(item).also { log(actorId, "CATALOG_MERGE_${it.status}", it.id) }
    }

    private fun log(actorId: Long, action: String, requestId: UUID) = audit.save(
        AuthorizationAuditEntity(actorUserId = actorId, action = action, detailJson = "{\"mergeRequestId\":\"$requestId\"}"),
    )
}
