package com.kazemieh.rasteh.services

import com.kazemieh.rasteh.shared.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/appointments")
class AppointmentController(private val service: AppointmentService) {
    @PostMapping
    fun book(@AuthenticationPrincipal p: UserPrincipal, @Valid @RequestBody req: CreateAppointmentRequest) = service.book(p.id, req)
    @GetMapping("/mine")
    fun mine(@AuthenticationPrincipal p: UserPrincipal) = service.listMine(p.id)
}

@RestController
@RequestMapping("/api/giftcards")
class GiftCardController(private val service: GiftCardService) {
    @PostMapping
    fun issue(@AuthenticationPrincipal p: UserPrincipal, @Valid @RequestBody req: CreateGiftCardRequest) = service.issue(p.id, req)
    @PostMapping("/redeem")
    fun redeem(@AuthenticationPrincipal p: UserPrincipal, @Valid @RequestBody req: RedeemGiftCardRequest) = service.redeem(p.id, req)
    @GetMapping("/mine")
    fun mine(@AuthenticationPrincipal p: UserPrincipal) = service.listMine(p.id)
}

@RestController
@RequestMapping("/api/returns")
class ReturnController(private val service: ReturnRequestService) {
    @PostMapping
    fun create(@AuthenticationPrincipal p: UserPrincipal, @Valid @RequestBody req: CreateReturnRequest) = service.create(p.id, req)
    @GetMapping("/mine")
    fun mine(@AuthenticationPrincipal p: UserPrincipal) = service.listMine(p.id)
}
