package com.kazemieh.rasteh.identity.membership.api

import com.kazemieh.rasteh.identity.authorization.AuthorizationService
import com.kazemieh.rasteh.identity.membership.application.MembershipService
import com.kazemieh.rasteh.identity.membership.persistence.entity.ShopMembershipRole
import com.kazemieh.rasteh.identity.membership.persistence.entity.PlatformRole
import com.kazemieh.rasteh.shared.security.UserPrincipal
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

data class InviteMembershipRequest(val userId: Long, val role: ShopMembershipRole)
@RestController @RequestMapping("/api/shops/{shopId}/memberships")
class ShopMembershipController(private val service: MembershipService, private val authorization: AuthorizationService) {
    @GetMapping fun list(@AuthenticationPrincipal p: UserPrincipal,@PathVariable shopId:Long)=service.list(p.id,shopId)
    @PostMapping fun invite(@AuthenticationPrincipal p:UserPrincipal,@PathVariable shopId:Long,@RequestBody req:InviteMembershipRequest)=service.invite(p.id,shopId,req.userId,req.role)
    @PostMapping("/{membershipId}/accept") fun accept(@AuthenticationPrincipal p:UserPrincipal,@PathVariable membershipId:UUID)=service.accept(p.id,membershipId)
    @DeleteMapping("/{membershipId}") fun revoke(@AuthenticationPrincipal p:UserPrincipal,@PathVariable membershipId:UUID)=service.revoke(p.id,membershipId)
}
@RestController @RequestMapping("/api/me")
class CapabilityController(private val authorization: AuthorizationService) {
    @GetMapping("/capabilities") fun capabilities(@AuthenticationPrincipal p:UserPrincipal,@RequestParam(required=false) shopId:Long?)=mapOf("capabilities" to authorization.capabilities(p.id,shopId).map { it.name })
}
data class PlatformRoleRequest(val userId:Long,val role:PlatformRole)
@RestController @RequestMapping("/api/admin/roles") class PlatformRoleController(private val service:MembershipService) {
    @PostMapping fun grant(@AuthenticationPrincipal p:UserPrincipal,@RequestBody r:PlatformRoleRequest)=service.grantPlatformRole(p.id,r.userId,r.role)
    @DeleteMapping fun revoke(@AuthenticationPrincipal p:UserPrincipal,@RequestParam userId:Long,@RequestParam role:PlatformRole)=service.revokePlatformRole(p.id,userId,role)
}
