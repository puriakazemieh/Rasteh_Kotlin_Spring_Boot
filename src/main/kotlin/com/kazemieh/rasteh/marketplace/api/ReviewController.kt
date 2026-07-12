package com.kazemieh.rasteh.marketplace.api

import com.kazemieh.rasteh.marketplace.api.dto.CreateReviewRequest
import com.kazemieh.rasteh.marketplace.application.ReviewService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reviews")
class ReviewController(
    private val reviewService: ReviewService,
) {
    /** نظراتِ یک فروشگاه (عمومی). */
    @GetMapping
    fun listByShop(@RequestParam shopId: Long) = reviewService.listByShop(shopId)

    /** ثبت/به‌روزرسانیِ نظر (احرازشده). */
    @PostMapping
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody req: CreateReviewRequest,
    ) = reviewService.create(principal.id, req)
}
