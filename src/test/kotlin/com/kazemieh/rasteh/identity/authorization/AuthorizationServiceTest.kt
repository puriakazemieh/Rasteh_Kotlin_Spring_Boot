package com.kazemieh.rasteh.identity.authorization

import com.kazemieh.rasteh.identity.membership.persistence.*
import com.kazemieh.rasteh.identity.membership.persistence.entity.*
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuthorizationServiceTest {
 @Test fun `revocation is immediate because active records are queried every time`() { val grants=mockk<PlatformRoleGrantRepository>();val memberships=mockk<ShopMembershipRepository>();every{grants.findAllByUserIdAndActiveTrue(7)} returns listOf(PlatformRoleGrantEntity(userId=7,role=PlatformRole.ADMIN,active=true)) andThen emptyList();every{memberships.findAllByUserIdAndStatus(7,MembershipStatus.ACTIVE)} returns emptyList();val policy=AuthorizationService(grants,memberships);assertThat(policy.can(7,Capability.AGENT_ASSIGN)).isTrue();assertThat(policy.can(7,Capability.AGENT_ASSIGN)).isFalse() }
}
