package com.kazemieh.rasteh.identity.authorization

import com.kazemieh.rasteh.identity.membership.persistence.entity.PlatformRole
import com.kazemieh.rasteh.identity.membership.persistence.entity.ShopMembershipRole

/** Single, explicit allow-list. A missing capability is deliberately denied. */
enum class Capability {
    PLATFORM_ROLE_MANAGE, SHOP_MEMBERSHIP_MANAGE, SHOP_VIEW_INTERNAL, SHOP_CATALOG_WRITE,
    SHOP_ORDER_OPERATE, SHOP_SUPPORT, SHOP_APPLICATION_REVIEW, AGENT_ASSIGN, AGENT_DRAFT_WRITE, AGENT_DRAFT_SUBMIT, AGENT_DRAFT_REVIEW
}

object AuthorizationCatalog {
    val platform: Map<PlatformRole, Set<Capability>> = mapOf(
        PlatformRole.SUPERADMIN to Capability.entries.toSet(),
        PlatformRole.ADMIN to setOf(Capability.SHOP_VIEW_INTERNAL, Capability.SHOP_APPLICATION_REVIEW, Capability.AGENT_ASSIGN, Capability.AGENT_DRAFT_REVIEW),
        PlatformRole.AGENT_SUPERVISOR to setOf(Capability.AGENT_ASSIGN, Capability.AGENT_DRAFT_REVIEW),
        PlatformRole.FIELD_AGENT to setOf(Capability.AGENT_DRAFT_WRITE, Capability.AGENT_DRAFT_SUBMIT),
        PlatformRole.USER to emptySet(),
    )
    val shop: Map<ShopMembershipRole, Set<Capability>> = mapOf(
        ShopMembershipRole.OWNER to setOf(Capability.SHOP_MEMBERSHIP_MANAGE, Capability.SHOP_VIEW_INTERNAL, Capability.SHOP_CATALOG_WRITE, Capability.SHOP_ORDER_OPERATE, Capability.SHOP_SUPPORT),
        ShopMembershipRole.MANAGER to setOf(Capability.SHOP_MEMBERSHIP_MANAGE, Capability.SHOP_VIEW_INTERNAL, Capability.SHOP_CATALOG_WRITE, Capability.SHOP_ORDER_OPERATE, Capability.SHOP_SUPPORT),
        ShopMembershipRole.CATALOG_EDITOR to setOf(Capability.SHOP_VIEW_INTERNAL, Capability.SHOP_CATALOG_WRITE),
        ShopMembershipRole.ORDER_OPERATOR to setOf(Capability.SHOP_VIEW_INTERNAL, Capability.SHOP_ORDER_OPERATE),
        ShopMembershipRole.SUPPORT to setOf(Capability.SHOP_VIEW_INTERNAL, Capability.SHOP_SUPPORT),
    )
}
