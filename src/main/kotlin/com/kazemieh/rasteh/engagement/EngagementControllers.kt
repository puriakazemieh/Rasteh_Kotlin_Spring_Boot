package com.kazemieh.rasteh.engagement

import com.kazemieh.rasteh.shared.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/events")
class EventController(private val service: EventService) {
    @GetMapping
    fun list(@RequestParam(required = false) locationId: Long?) = service.list(locationId)
}

@RestController
@RequestMapping("/api/referral")
class ReferralController(private val service: ReferralService) {
    @GetMapping("/me")
    fun me(@AuthenticationPrincipal p: UserPrincipal) = service.myReferral(p.id)

    @PostMapping("/redeem")
    fun redeem(@AuthenticationPrincipal p: UserPrincipal, @Valid @RequestBody req: RedeemReferralRequest) = service.redeem(p.id, req)
}
