package com.kazemieh.rasteh.marketplace.application

import com.kazemieh.rasteh.marketplace.api.dto.CreateReportRequest
import com.kazemieh.rasteh.marketplace.api.dto.ReportResponse
import com.kazemieh.rasteh.marketplace.persistence.ReportRepository
import com.kazemieh.rasteh.marketplace.persistence.entity.ReportEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReportService(
    private val reportRepository: ReportRepository,
) {
    private fun toResponse(r: ReportEntity) = ReportResponse(
        id = r.id, userId = r.userId, targetType = r.targetType, targetId = r.targetId,
        reason = r.reason, status = r.status, createdAt = r.createdAt,
    )

    @Transactional
    fun create(userId: Long, req: CreateReportRequest): ReportResponse =
        toResponse(
            reportRepository.save(
                ReportEntity(
                    userId = userId,
                    targetType = req.targetType.trim().uppercase(),
                    targetId = req.targetId,
                    reason = req.reason?.trim(),
                    status = "OPEN",
                )
            )
        )

    @Transactional(readOnly = true)
    fun listAll(status: String?): List<ReportResponse> =
        (if (status.isNullOrBlank()) reportRepository.findAllByOrderByIdDesc()
        else reportRepository.findAllByStatusOrderByIdDesc(status.trim().uppercase()))
            .map(::toResponse)

    @Transactional
    fun resolve(id: Long, status: String): ReportResponse? {
        val report = reportRepository.findById(id).orElse(null) ?: return null
        report.status = status.trim().uppercase()
        return toResponse(report)
    }
}
