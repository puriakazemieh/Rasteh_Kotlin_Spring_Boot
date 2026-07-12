package com.kazemieh.rasteh.interaction.api

import com.kazemieh.rasteh.interaction.api.dto.CreateBookmarkRequest
import com.kazemieh.rasteh.interaction.application.BookmarkService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bookmarks")
class BookmarkController(
    private val bookmarkService: BookmarkService,
) {
    /** فهرستِ نشان‌شده‌هایِ من (فروشگاه/کالا). */
    @GetMapping
    fun mine(@AuthenticationPrincipal principal: UserPrincipal) =
        bookmarkService.listMine(principal.id)

    /** افزودنِ نشان (idempotent). */
    @PostMapping
    fun add(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestBody req: CreateBookmarkRequest,
    ) = bookmarkService.add(principal.id, req)

    @DeleteMapping("/{id}")
    fun remove(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        bookmarkService.remove(principal.id, id)
        return ResponseEntity.noContent().build()
    }
}
