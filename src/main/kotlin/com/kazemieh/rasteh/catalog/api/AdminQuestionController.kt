package com.kazemieh.rasteh.catalog.api

import com.kazemieh.rasteh.catalog.api.dto.AdminInteractionResponse
import com.kazemieh.rasteh.catalog.api.dto.PageResponse
import com.kazemieh.rasteh.catalog.application.QuestionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/questions")
class AdminQuestionController(
    private val questionService: QuestionService
) {

    @GetMapping
    fun list(
        @RequestParam(required = false) productId: Long?,
        @RequestParam(required = false) isNew: Boolean?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): PageResponse<AdminInteractionResponse> {
        return questionService.listQuestionsAdmin(productId, isNew, page, size)
    }
}
