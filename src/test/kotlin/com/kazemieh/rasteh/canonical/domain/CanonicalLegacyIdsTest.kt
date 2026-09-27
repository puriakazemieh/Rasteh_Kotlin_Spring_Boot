package com.kazemieh.rasteh.canonical.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CanonicalLegacyIdsTest {
    @Test
    fun `legacy identifiers are stable and scoped by canonical entity kind`() {
        assertThat(CanonicalLegacyIds.merchantOrganization(42))
            .isEqualTo(CanonicalLegacyIds.merchantOrganization(42))
        assertThat(CanonicalLegacyIds.merchantOrganization(42))
            .isNotEqualTo(CanonicalLegacyIds.shopLocation(42))
        assertThat(CanonicalLegacyIds.canonicalProductForShopProduct(42))
            .isNotEqualTo(CanonicalLegacyIds.canonicalVariantForShopProduct(42))
    }
}
