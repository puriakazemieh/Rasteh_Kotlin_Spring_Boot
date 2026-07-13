package com.kazemieh.rasteh.advanced

import com.kazemieh.rasteh.shared.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

private fun UserPrincipal.isAdmin(): Boolean =
    authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_SUPERADMIN" }

@RestController
@RequestMapping("/api/community")
class CommunityController(private val service: CommunityService) {
    @GetMapping
    fun posts() = service.listPosts()
    @PostMapping
    fun create(@AuthenticationPrincipal p: UserPrincipal, @Valid @RequestBody req: CreatePostRequest) = service.createPost(p.id, req)
    @GetMapping("/{id}/comments")
    fun comments(@PathVariable id: Long) = service.listComments(id)
    @PostMapping("/{id}/comments")
    fun comment(@AuthenticationPrincipal p: UserPrincipal, @PathVariable id: Long, @Valid @RequestBody req: CreateCommentRequest) = service.addComment(p.id, id, req)
}

@RestController
@RequestMapping("/api/subscription")
class SubscriptionController(private val service: SubscriptionService) {
    @GetMapping("/me")
    fun me(@AuthenticationPrincipal p: UserPrincipal) = service.me(p.id)
    @PostMapping("/subscribe")
    fun subscribe(@AuthenticationPrincipal p: UserPrincipal) = service.subscribe(p.id)
}

@RestController
@RequestMapping("/api/notifications")
class NotificationController(private val service: NotificationService) {
    @GetMapping
    fun mine(@AuthenticationPrincipal p: UserPrincipal) = service.listMine(p.id)
    @PostMapping("/{id}/read")
    fun read(@AuthenticationPrincipal p: UserPrincipal, @PathVariable id: Long): ResponseEntity<Void> {
        service.markRead(p.id, id)
        return ResponseEntity.noContent().build()
    }
}

@RestController
@RequestMapping("/api/vendor/analytics")
class VendorAnalyticsController(private val service: VendorAnalyticsService) {
    @GetMapping("/shop/{shopId}")
    fun forShop(@AuthenticationPrincipal p: UserPrincipal, @PathVariable shopId: Long) = service.forShop(p.id, p.isAdmin(), shopId)
}
