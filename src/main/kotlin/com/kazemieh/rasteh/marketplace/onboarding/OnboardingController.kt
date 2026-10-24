package com.kazemieh.rasteh.marketplace.onboarding

import com.kazemieh.rasteh.shared.security.UserPrincipal
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController @RequestMapping("/api/shop-applications") class OnboardingController(private val service:OnboardingService){
 @PostMapping fun draft(@AuthenticationPrincipal p:UserPrincipal,@RequestBody r:ApplicationRequest)=service.draft(p.id,r)
 @PostMapping("/{id}/consent") fun consent(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID)=service.consent(p.id,id)
 @PostMapping("/{id}/submit") fun submit(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID)=service.submit(p.id,id)
 @PostMapping("/{id}/evidence") fun evidence(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID,@RequestBody r:EvidenceRequest)=service.addEvidence(p.id,id,r)
 @GetMapping("/{id}/evidence") fun evidence(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID)=service.evidenceFor(p.id,id)
 @PostMapping("/{id}/claim") fun claim(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID)=service.startClaim(p.id,id)
 @PostMapping("/{id}/claim/verify") fun verify(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID,@RequestParam otp:String)=service.verifyClaimOtp(p.id,id,otp)
}
@RestController @RequestMapping("/api/admin/shop-applications") class AdminOnboardingController(private val service:OnboardingService){
 @GetMapping fun queue(@AuthenticationPrincipal p:UserPrincipal,@RequestParam(defaultValue="SUBMITTED") status:ApplicationStatus)=service.queue(p.id,status)
 @PostMapping("/{id}/review") fun review(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID,@RequestBody r:ReviewApplicationRequest)=service.review(p.id,id,r)
 @PostMapping("/{id}/claim/approve") fun approveClaim(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID)=service.approveClaim(p.id,id)
}
@RestController @RequestMapping("/api/geo") class GeoController(private val geo:GeoSearchService){@GetMapping("/clusters/nearby") fun nearby(@RequestParam lat:Double,@RequestParam lng:Double,@RequestParam(defaultValue="5000") radiusMeters:Double)=geo.nearby(lat,lng,radiusMeters)}
