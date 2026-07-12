package com.kazemieh.rasteh.interaction.api

import com.kazemieh.rasteh.interaction.api.dto.SendMessageRequest
import com.kazemieh.rasteh.interaction.api.dto.StartConversationRequest
import com.kazemieh.rasteh.interaction.application.ChatService
import com.kazemieh.rasteh.shared.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatService: ChatService,
) {
    private fun UserPrincipal.isAdmin(): Boolean =
        authorities.any { it.authority == "ROLE_ADMIN" || it.authority == "ROLE_SUPERADMIN" }

    /** ساختن/گرفتنِ گفت‌وگو با فروشگاه (خریدار). */
    @PostMapping("/conversations")
    fun start(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody req: StartConversationRequest,
    ) = chatService.startOrGet(principal.id, req.shopId)

    /** گفت‌وگوهایِ من (خریدار + فروشنده). */
    @GetMapping("/conversations")
    fun mine(@AuthenticationPrincipal principal: UserPrincipal) =
        chatService.listMine(principal.id)

    @GetMapping("/conversations/{id}/messages")
    fun messages(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
    ) = chatService.listMessages(principal.id, principal.isAdmin(), id)

    @PostMapping("/conversations/{id}/messages")
    fun send(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
        @Valid @RequestBody req: SendMessageRequest,
    ) = chatService.sendMessage(principal.id, principal.isAdmin(), id, req.body)
}
