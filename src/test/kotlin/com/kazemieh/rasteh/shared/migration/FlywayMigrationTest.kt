package com.kazemieh.rasteh.shared.migration

import com.kazemieh.rasteh.canonical.application.CanonicalBackfillDryRunService
import com.kazemieh.rasteh.canonical.application.CanonicalMarketplaceBackfillService
import com.kazemieh.rasteh.canonical.persistence.ShopOfferRepository
import com.kazemieh.rasteh.canonical.persistence.MerchantOrganizationRepository
import com.kazemieh.rasteh.catalog.persistence.ProductRepository
import com.kazemieh.rasteh.catalog.persistence.ProductVariantRepository
import com.kazemieh.rasteh.catalog.persistence.entity.ProductEntity
import com.kazemieh.rasteh.catalog.persistence.entity.ProductVariantEntity
import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.identity.persistence.entity.UserEntity
import com.kazemieh.rasteh.marketplace.domain.ShopStatus
import com.kazemieh.rasteh.marketplace.domain.ShopType
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopEntity
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopProductEntity
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.springframework.core.io.ClassPathResource
import java.security.SecureRandom
import java.util.Base64
import java.sql.DriverManager
import java.math.BigDecimal

@Testcontainers
@SpringBootTest(properties = [
    "spring.jpa.hibernate.ddl-auto=validate",
    "app.seed.enabled=false",
    "app.canonical-marketplace.writes-enabled=true",
    "spring.flyway.baseline-on-migrate=false",
])
class FlywayMigrationTest @Autowired constructor(
    private val flyway: Flyway,
    private val dryRunService: CanonicalBackfillDryRunService,
    private val backfillService: CanonicalMarketplaceBackfillService,
    private val users: UserRepository,
    private val shops: ShopRepository,
    private val shopProducts: ShopProductRepository,
    private val catalogProducts: ProductRepository,
    private val catalogVariants: ProductVariantRepository,
    private val offers: ShopOfferRepository,
    private val organizations: MerchantOrganizationRepository,
) {
    @Test
    fun `an empty PostgreSQL database reaches the complete local migration version`() {
        val migrations = flyway.info().applied().mapNotNull { it.version?.version }

        assertThat(migrations).containsExactly("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    }

    @Test
    fun `canonical marketplace expansion creates isolated tables without inferred mappings`() {
        DriverManager.getConnection(postgres.jdbcUrl, postgres.username, postgres.password).use { connection ->
            connection.metaData.getTables(null, null, "%", arrayOf("TABLE")).use { tables ->
                val tableNames = generateSequence { if (tables.next()) tables.getString("TABLE_NAME") else null }
                    .map(String::lowercase)
                    .toSet()

                assertThat(tableNames).contains(
                    "merchant_organizations",
                    "shop_locations_v1",
                    "canonical_products",
                    "canonical_variants",
                    "shop_offers",
                    "legacy_product_crosswalk",
                    "geo_clusters",
                    "shop_applications",
                    "application_evidence",
                    "shop_claims",
                )
            }

            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT COUNT(*) FROM legacy_product_crosswalk").use { rows ->
                    rows.next()
                    assertThat(rows.getLong(1)).isZero()
                }
            }
        }
    }

    @Test
    fun `dry run and idempotent backfill reconcile synthetic multi shop legacy data`() {
        val owner = users.save(UserEntity(email = "canonical-owner@example.invalid", passwordHash = "test"))
        val shop = shops.save(
            ShopEntity(owner = owner, name = "Canonical test shop", type = ShopType.BUYABLE, status = ShopStatus.APPROVED),
        )
        val catalogProduct = catalogProducts.save(ProductEntity(title = "Catalog product", slug = "canonical-test-product"))
        catalogVariants.save(ProductVariantEntity(product = catalogProduct, sku = "CANONICAL-TEST", price = BigDecimal("100000")))
        val legacyProduct = shopProducts.save(
            ShopProductEntity(shop = shop, name = "Legacy product", price = BigDecimal("125000"), stock = 2),
        )
        val secondOwner = users.save(UserEntity(email = "canonical-owner-two@example.invalid", passwordHash = "test"))
        val showcaseShop = shops.save(
            ShopEntity(owner = secondOwner, name = "Showcase test shop", type = ShopType.VISIT_ONLY, status = ShopStatus.APPROVED),
        )
        val showcaseProduct = shopProducts.save(
            ShopProductEntity(shop = showcaseShop, name = "Independent legacy product", price = BigDecimal("99000"), stock = 1),
        )

        assertThat(dryRunService.report().safeToBackfill).isTrue()
        assertThat(backfillService.backfill().canonicalOfferCount).isEqualTo(2)
        assertThat(backfillService.backfill().canonicalOfferCount).isEqualTo(2)

        val offer = offers.findAll().single { it.externalShopProductId == legacyProduct.id }
        assertThat(offer.externalShopProductId).isEqualTo(legacyProduct.id)
        assertThat(offer.priceMinorUnits).isEqualTo(125000L)
        assertThat(offer.onHand).isEqualTo(2)
        assertThat(offers.findAll().single { it.externalShopProductId == showcaseProduct.id }.saleMode.name)
            .isEqualTo("SHOWCASE")
        assertThat(organizations.count()).isEqualTo(2)
    }

    @Test
    fun `a local baseline snapshot is upgraded without replaying its baseline`() {
        DriverManager.getConnection(legacyDatabaseUrl(), postgres.username, postgres.password).use { connection ->
            val baselineSql = ClassPathResource("db/migration/V0__local_schema_baseline.sql")
                .inputStream.bufferedReader().use { it.readText() }
            connection.createStatement().use { statement ->
                baselineSql.splitToSequence(";")
                    .map(String::trim)
                    .filter(String::isNotBlank)
                    .forEach(statement::execute)
            }
        }

        val migrated = Flyway.configure()
            .dataSource(legacyDatabaseUrl(), postgres.username, postgres.password)
            .locations("classpath:db/migration")
            .baselineOnMigrate(true)
            .baselineVersion("0")
            .load()
            .also { it.migrate() }

        assertThat(migrated.info().applied().mapNotNull { it.version?.version })
            .containsExactly("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    }

    private fun legacyDatabaseUrl(): String =
        "jdbc:postgresql://${postgres.host}:${postgres.getMappedPort(5432)}/postgres"

    companion object {
        @Container
        @JvmStatic
        val postgres = LocalPostgresContainer()

        private val jwtSecret = ByteArray(48).also(SecureRandom()::nextBytes)
            .let(Base64.getEncoder()::encodeToString)

        @DynamicPropertySource
        @JvmStatic
        fun databaseProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("jwt.secret-key") { jwtSecret }
        }
    }

    class LocalPostgresContainer : PostgreSQLContainer<LocalPostgresContainer>("postgres:16-alpine")
}
