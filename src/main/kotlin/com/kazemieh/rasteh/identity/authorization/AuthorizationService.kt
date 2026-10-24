package com.kazemieh.rasteh.identity.authorization

import com.kazemieh.rasteh.identity.membership.persistence.*
import com.kazemieh.rasteh.identity.membership.persistence.entity.*
import com.kazemieh.rasteh.shared.error.ForbiddenException
import org.springframework.stereotype.Service

@Service
class AuthorizationService(private val grants: PlatformRoleGrantRepository, private val memberships: ShopMembershipRepository) {
    fun can(userId: Long, capability: Capability, shopId: Long? = null): Boolean {
        val platformAllowed = grants.findAllByUserIdAndActiveTrue(userId).any { capability in (AuthorizationCatalog.platform[it.role] ?: emptySet()) }
        if (platformAllowed) return true
        return shopId != null && memberships.findAllByUserIdAndStatus(userId, MembershipStatus.ACTIVE)
            .filter { it.shopId == shopId }.any { capability in (AuthorizationCatalog.shop[it.role] ?: emptySet()) }
    }
    fun require(userId: Long, capability: Capability, shopId: Long? = null) {
        if (!can(userId, capability, shopId)) throw ForbiddenException("Capability $capability is not granted for this resource", "CAPABILITY_DENIED")
    }
    fun capabilities(userId: Long, shopId: Long? = null): Set<Capability> = Capability.entries.filter { can(userId, it, shopId) }.toSet()
}
