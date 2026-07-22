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

@RestController
@RequestMapping("/api/warranties")
class WarrantyController(private val service: WarrantyService) {
    @PostMapping
    fun create(@AuthenticationPrincipal p: UserPrincipal, @Valid @RequestBody req: CreateWarrantyRequest) = service.create(p.id, req)
    @GetMapping("/mine")
    fun mine(@AuthenticationPrincipal p: UserPrincipal) = service.listMine(p.id)
}

@RestController
@RequestMapping("/api/activity")
class ActivityController(private val service: ActivityService) {
    @GetMapping
    fun feed(@AuthenticationPrincipal p: UserPrincipal) = service.feed(p.id)
}

@RestController
@RequestMapping("/api/parking")
class ParkingController(private val service: ParkingService) {
    @PostMapping("/checkin")
    fun checkin(@AuthenticationPrincipal p: UserPrincipal, @Valid @RequestBody req: CheckinParkingRequest) = service.checkin(p.id, req)
    @GetMapping("/mine")
    fun mine(@AuthenticationPrincipal p: UserPrincipal) = service.listMine(p.id)
    @PostMapping("/{id}/pay")
    fun pay(@AuthenticationPrincipal p: UserPrincipal, @PathVariable id: Long) = service.pay(p.id, id)
}

@RestController
@RequestMapping("/api/live")
class LiveSessionController(private val service: LiveSessionService) {
    @GetMapping
    fun live() = service.listLive()
}

@RestController
@RequestMapping("/api/escrow")
class EscrowController(private val service: EscrowService) {
    @PostMapping
    fun open(@AuthenticationPrincipal p: UserPrincipal, @Valid @RequestBody req: CreateEscrowRequest) = service.open(p.id, req)
    @GetMapping("/mine")
    fun mine(@AuthenticationPrincipal p: UserPrincipal) = service.listMine(p.id)
    @PostMapping("/{id}/release")
    fun release(@AuthenticationPrincipal p: UserPrincipal, @PathVariable id: Long) = service.release(p.id, id)
}
