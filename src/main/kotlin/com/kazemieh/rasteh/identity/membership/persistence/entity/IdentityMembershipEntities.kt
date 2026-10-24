package com.kazemieh.rasteh.identity.membership.persistence.entity

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

enum class PlatformRole { USER, FIELD_AGENT, AGENT_SUPERVISOR, ADMIN, SUPERADMIN }
enum class ShopMembershipRole { OWNER, MANAGER, CATALOG_EDITOR, ORDER_OPERATOR, SUPPORT }
enum class MembershipStatus { INVITED, ACTIVE, REVOKED }

@Entity @Table(name = "platform_role_grants")
class PlatformRoleGrantEntity(@Id var id: UUID = UUID.randomUUID(), @Column(name="user_id") var userId: Long = 0,
    @Enumerated(EnumType.STRING) var role: PlatformRole = PlatformRole.USER, var active: Boolean = true,
    @Column(name="granted_by_user_id") var grantedByUserId: Long? = null, @Column(name="revoked_by_user_id") var revokedByUserId: Long? = null,
    @Column(name="revoked_at") var revokedAt: OffsetDateTime? = null, @Column(name="created_at") var createdAt: OffsetDateTime? = null)

@Entity @Table(name = "shop_memberships")
class ShopMembershipEntity(@Id var id: UUID = UUID.randomUUID(), @Column(name="shop_id") var shopId: Long = 0,
    @Column(name="user_id") var userId: Long = 0, @Enumerated(EnumType.STRING) var role: ShopMembershipRole = ShopMembershipRole.SUPPORT,
    @Enumerated(EnumType.STRING) var status: MembershipStatus = MembershipStatus.ACTIVE,
    @Column(name="invited_by_user_id") var invitedByUserId: Long? = null, @Column(name="accepted_at") var acceptedAt: OffsetDateTime? = null,
    @Column(name="revoked_by_user_id") var revokedByUserId: Long? = null, @Column(name="revoked_at") var revokedAt: OffsetDateTime? = null,
    @Column(name="created_at") var createdAt: OffsetDateTime? = null)
