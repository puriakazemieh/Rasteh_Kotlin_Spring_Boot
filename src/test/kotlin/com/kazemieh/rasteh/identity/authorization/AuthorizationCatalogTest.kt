package com.kazemieh.rasteh.identity.authorization

import com.kazemieh.rasteh.identity.membership.persistence.entity.*
import com.kazemieh.rasteh.shared.security.UserPrincipal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuthorizationCatalogTest {
 @Test fun `multi authority principal exposes all active grants`() { val p=UserPrincipal(1,"u","p",setOf("FIELD_AGENT","ADMIN"),true); assertThat(p.authorities.map{it.authority}).containsExactlyInAnyOrder("ROLE_FIELD_AGENT","ROLE_ADMIN") }
 @Test fun `least privilege catalog isolates shop roles`() { assertThat(AuthorizationCatalog.shop.getValue(ShopMembershipRole.CATALOG_EDITOR)).contains(Capability.SHOP_CATALOG_WRITE).doesNotContain(Capability.SHOP_ORDER_OPERATE,Capability.SHOP_MEMBERSHIP_MANAGE); assertThat(AuthorizationCatalog.shop.getValue(ShopMembershipRole.SUPPORT)).contains(Capability.SHOP_SUPPORT).doesNotContain(Capability.SHOP_CATALOG_WRITE) }
 @Test fun `maker checker and assignment capabilities are separated`() { assertThat(AuthorizationCatalog.platform.getValue(PlatformRole.FIELD_AGENT)).contains(Capability.AGENT_DRAFT_WRITE,Capability.AGENT_DRAFT_SUBMIT).doesNotContain(Capability.AGENT_DRAFT_REVIEW,Capability.AGENT_ASSIGN); assertThat(AuthorizationCatalog.platform.getValue(PlatformRole.AGENT_SUPERVISOR)).contains(Capability.AGENT_ASSIGN,Capability.AGENT_DRAFT_REVIEW).doesNotContain(Capability.AGENT_DRAFT_WRITE) }
}
