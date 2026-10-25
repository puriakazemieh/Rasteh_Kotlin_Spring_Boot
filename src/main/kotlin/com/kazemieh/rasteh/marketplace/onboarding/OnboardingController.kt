package com.kazemieh.rasteh.marketplace.onboarding

import com.kazemieh.rasteh.shared.security.UserPrincipal
import com.kazemieh.rasteh.identity.authorization.AuthorizationService
import com.kazemieh.rasteh.identity.authorization.Capability
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController @RequestMapping("/api/shop-applications") class OnboardingController(private val service:OnboardingService){
 @PostMapping fun draft(@AuthenticationPrincipal p:UserPrincipal,@RequestBody r:ApplicationRequest)=service.draft(p.id,r)
 @PostMapping("/{id}/consent") fun consent(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID)=service.consent(p.id,id)
 @PostMapping("/{id}/submit") fun submit(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID)=service.submit(p.id,id)
 @PostMapping("/{id}/appeal") fun appeal(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID,@RequestBody body:Map<String,String>)=service.appeal(p.id,id,body["text"].orEmpty())
 @PostMapping("/{id}/evidence") fun evidence(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID,@RequestBody r:EvidenceRequest)=service.addEvidence(p.id,id,r)
 @GetMapping("/{id}/evidence") fun evidence(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID)=service.evidenceFor(p.id,id)
 @PostMapping("/{id}/claim") fun claim(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID)=service.startClaim(p.id,id)
 @PostMapping("/{id}/claim/verify") fun verify(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID,@RequestParam otp:String)=service.verifyClaimOtp(p.id,id,otp)
}
data class AgentApplicationRequest(val applicantUserId:Long,val application:ApplicationRequest)
@RestController @RequestMapping("/api/agent/shop-applications") class AgentOnboardingController(private val service:OnboardingService,private val authorization:AuthorizationService){
 @PostMapping fun draft(@AuthenticationPrincipal p:UserPrincipal,@RequestBody r:AgentApplicationRequest):Any { authorization.require(p.id,Capability.AGENT_DRAFT_WRITE);return service.draft(r.applicantUserId,r.application,p.id) }
}
@RestController @RequestMapping("/api/admin/shop-applications") class AdminOnboardingController(private val service:OnboardingService){
 @GetMapping fun queue(@AuthenticationPrincipal p:UserPrincipal,@RequestParam(defaultValue="SUBMITTED") status:ApplicationStatus)=service.queue(p.id,status)
 @PostMapping("/{id}/review") fun review(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID,@RequestBody r:ReviewApplicationRequest)=service.review(p.id,id,r)
 @PostMapping("/{id}/claim/approve") fun approveClaim(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID)=service.approveClaim(p.id,id)
}
@RestController @RequestMapping("/api/geo") class GeoController(private val geo:GeoSearchService){@GetMapping("/clusters/nearby") fun nearby(@RequestParam lat:Double,@RequestParam lng:Double,@RequestParam(defaultValue="5000") radiusMeters:Double)=geo.nearby(lat,lng,radiusMeters)}
