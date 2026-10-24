package com.kazemieh.rasteh.identity.membership.application

import com.kazemieh.rasteh.identity.authorization.*
import com.kazemieh.rasteh.identity.membership.persistence.*
import com.kazemieh.rasteh.identity.membership.persistence.entity.*
import com.kazemieh.rasteh.shared.error.BadRequestException
import com.kazemieh.rasteh.shared.error.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service class MembershipService(private val authorization: AuthorizationService, private val memberships: ShopMembershipRepository, private val audits: AuthorizationAuditRepository, private val grants: PlatformRoleGrantRepository) {
    @Transactional fun invite(actor: Long, shopId: Long, userId: Long, role: ShopMembershipRole): ShopMembershipEntity {
        authorization.require(actor, Capability.SHOP_MEMBERSHIP_MANAGE, shopId)
        if (role == ShopMembershipRole.OWNER) throw BadRequestException("Ownership transfer is not supported", "OWNERSHIP_TRANSFER_FORBIDDEN")
        if (memberships.findAllByShopId(shopId).any { it.userId == userId && it.role == role && it.status != MembershipStatus.REVOKED }) throw BadRequestException("Membership already exists", "MEMBERSHIP_EXISTS")
        return memberships.save(ShopMembershipEntity(shopId=shopId,userId=userId,role=role,status=MembershipStatus.INVITED,invitedByUserId=actor)).also { audit(actor,userId,shopId,"SHOP_MEMBERSHIP_INVITED") }
    }
    @Transactional fun accept(userId: Long, membershipId: UUID): ShopMembershipEntity { val item=memberships.findByIdAndUserIdAndStatus(membershipId,userId,MembershipStatus.INVITED) ?: throw NotFoundException("Invitation not found", "INVITATION_NOT_FOUND"); item.status=MembershipStatus.ACTIVE; item.acceptedAt=OffsetDateTime.now(); audit(userId,userId,item.shopId,"SHOP_MEMBERSHIP_ACCEPTED"); return memberships.save(item) }
    @Transactional fun revoke(actor: Long, membershipId: UUID) { val item=memberships.findById(membershipId).orElseThrow { NotFoundException("Membership not found", "MEMBERSHIP_NOT_FOUND") }; authorization.require(actor,Capability.SHOP_MEMBERSHIP_MANAGE,item.shopId); if(item.role==ShopMembershipRole.OWNER) throw BadRequestException("Ownership transfer/revocation is forbidden","OWNERSHIP_TRANSFER_FORBIDDEN"); item.status=MembershipStatus.REVOKED; item.revokedAt=OffsetDateTime.now(); item.revokedByUserId=actor; memberships.save(item); audit(actor,item.userId,item.shopId,"SHOP_MEMBERSHIP_REVOKED") }
    fun list(actor: Long, shopId: Long): List<ShopMembershipEntity> { authorization.require(actor,Capability.SHOP_MEMBERSHIP_MANAGE,shopId); return memberships.findAllByShopId(shopId) }
    @Transactional fun grantPlatformRole(actor:Long,userId:Long,role:PlatformRole):PlatformRoleGrantEntity { authorization.require(actor,Capability.PLATFORM_ROLE_MANAGE); if(role==PlatformRole.SUPERADMIN) throw BadRequestException("Superadmin grants require break-glass process","BREAK_GLASS_REQUIRED"); val existing=grants.findByUserIdAndRoleAndActiveTrue(userId,role); if(existing!=null)return existing; return grants.save(PlatformRoleGrantEntity(userId=userId,role=role,grantedByUserId=actor)).also{audit(actor,userId,null,"PLATFORM_ROLE_GRANTED")} }
    @Transactional fun revokePlatformRole(actor:Long,userId:Long,role:PlatformRole) { authorization.require(actor,Capability.PLATFORM_ROLE_MANAGE); if(role==PlatformRole.SUPERADMIN) throw BadRequestException("Superadmin revoke requires break-glass process","BREAK_GLASS_REQUIRED"); val item=grants.findByUserIdAndRoleAndActiveTrue(userId,role)?:return;item.active=false;item.revokedAt=OffsetDateTime.now();item.revokedByUserId=actor;grants.save(item);audit(actor,userId,null,"PLATFORM_ROLE_REVOKED") }
    private fun audit(actor:Long,subject:Long,shop:Long?,action:String)=audits.save(AuthorizationAuditEntity(actorUserId=actor,subjectUserId=subject,shopId=shop,action=action))
}
