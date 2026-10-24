package com.kazemieh.rasteh.identity.membership.persistence
import com.kazemieh.rasteh.identity.membership.persistence.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID
interface PlatformRoleGrantRepository: JpaRepository<PlatformRoleGrantEntity, UUID> { fun findAllByUserIdAndActiveTrue(userId: Long): List<PlatformRoleGrantEntity>; fun findByUserIdAndRoleAndActiveTrue(userId: Long, role: PlatformRole): PlatformRoleGrantEntity? }
interface ShopMembershipRepository: JpaRepository<ShopMembershipEntity, UUID> {
    fun findAllByUserIdAndStatus(userId: Long, status: MembershipStatus): List<ShopMembershipEntity>
    fun findAllByShopId(shopId: Long): List<ShopMembershipEntity>
    fun findByShopIdAndUserIdAndRoleAndStatus(shopId:Long,userId:Long,role:ShopMembershipRole,status:MembershipStatus): ShopMembershipEntity?
    fun findByIdAndUserIdAndStatus(id: UUID, userId: Long, status: MembershipStatus): ShopMembershipEntity?
}
interface AuthorizationAuditRepository: JpaRepository<AuthorizationAuditEntity, UUID>
