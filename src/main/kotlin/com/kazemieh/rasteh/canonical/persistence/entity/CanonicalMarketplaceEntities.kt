package com.kazemieh.rasteh.canonical.persistence.entity

import com.kazemieh.rasteh.canonical.domain.SaleMode
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "merchant_organizations")
class MerchantOrganizationEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "external_shop_id", nullable = false, unique = true) var externalShopId: Long = 0,
    @Column(name = "owner_user_id", nullable = false) var ownerUserId: Long = 0,
    @Column(name = "display_name", nullable = false) var displayName: String = "",
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) var createdAt: OffsetDateTime? = null,
)

@Entity
@Table(name = "shop_locations_v1")
class ShopLocationEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "merchant_organization_id", nullable = false, unique = true) var merchantOrganizationId: UUID = UUID.randomUUID(),
    @Column(name = "external_shop_id", nullable = false, unique = true) var externalShopId: Long = 0,
    @Column(name = "external_location_id") var externalLocationId: Long? = null,
    @Column(nullable = false) var name: String = "",
    @Column(columnDefinition = "text") var address: String? = null,
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) var createdAt: OffsetDateTime? = null,
)

@Entity
@Table(name = "canonical_products")
class CanonicalProductEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "external_catalog_product_id", unique = true) var externalCatalogProductId: Long? = null,
    @Column(name = "external_shop_product_id", unique = true) var externalShopProductId: Long? = null,
    @Column(nullable = false) var title: String = "",
    var slug: String? = null,
    @Column(columnDefinition = "text") var description: String? = null,
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) var createdAt: OffsetDateTime? = null,
)

@Entity
@Table(name = "canonical_variants")
class CanonicalVariantEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "product_id", nullable = false) var productId: UUID = UUID.randomUUID(),
    @Column(name = "external_catalog_variant_id", unique = true) var externalCatalogVariantId: Long? = null,
    var sku: String? = null,
    @Column(nullable = false) var title: String = "",
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) var createdAt: OffsetDateTime? = null,
)

@Entity
@Table(name = "shop_offers")
class ShopOfferEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "shop_location_id", nullable = false) var shopLocationId: UUID = UUID.randomUUID(),
    @Column(name = "product_id", nullable = false) var productId: UUID = UUID.randomUUID(),
    @Column(name = "variant_id") var variantId: UUID? = null,
    @Column(name = "external_shop_product_id", unique = true) var externalShopProductId: Long? = null,
    @Column(name = "price_minor_units") var priceMinorUnits: Long? = null,
    @Column(nullable = false, length = 3) var currency: String = "IRR",
    @Enumerated(EnumType.STRING) @Column(name = "sale_mode", nullable = false) var saleMode: SaleMode = SaleMode.SHOWCASE,
    @Column(name = "on_hand", nullable = false) var onHand: Int = 0,
    @Column(nullable = false) var reserved: Int = 0,
    @Column(nullable = false) var active: Boolean = true,
    @Column(name = "last_confirmed_at") var lastConfirmedAt: OffsetDateTime? = null,
    @Column(name = "expires_at") var expiresAt: OffsetDateTime? = null,
    @Column(nullable = false) var stale: Boolean = false,
    @Version @Column(nullable = false) var version: Long = 0,
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) var createdAt: OffsetDateTime? = null,
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) var updatedAt: OffsetDateTime? = null,
)

@Entity @Table(name = "inventory_ledger")
class InventoryLedgerEntity(@Id var id: UUID = UUID.randomUUID(), @Column(name="offer_id") var offerId: UUID = UUID.randomUUID(),
    @Column(name="delta_on_hand") var deltaOnHand: Int = 0, @Column(name="delta_reserved") var deltaReserved: Int = 0,
    var reason: String = "", @Column(name="idempotency_key") var idempotencyKey: String = "", @Column(name="actor_user_id") var actorUserId: Long? = null)
@Entity @Table(name = "offer_reservations")
class OfferReservationEntity(@Id var id: UUID = UUID.randomUUID(), @Column(name="offer_id") var offerId: UUID = UUID.randomUUID(),
    var quantity: Int = 0, var status: String = "ACTIVE", @Column(name="expires_at") var expiresAt: OffsetDateTime = OffsetDateTime.now(), @Column(name="idempotency_key") var idempotencyKey: String = "")

@Entity @Table(name = "offer_media")
class OfferMediaEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "offer_id") var offerId: UUID = UUID.randomUUID(),
    @Column(name = "object_key") var objectKey: String = "",
    @Column(name = "media_type") var mediaType: String = "image/jpeg",
    var position: Int = 0,
)

@Entity @Table(name = "catalog_events")
class CatalogEventEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "event_type") var eventType: String = "",
    @Column(name = "offer_id") var offerId: UUID? = null,
    @Column(name = "payload_json", columnDefinition = "jsonb") var payloadJson: String = "{}",
    @CreationTimestamp @Column(name = "created_at", updatable = false) var createdAt: OffsetDateTime? = null,
)

enum class CatalogMergeStatus { PENDING, APPROVED, REJECTED }

@Entity @Table(name = "catalog_merge_queue")
class CatalogMergeQueueEntity(
    @Id var id: UUID = UUID.randomUUID(),
    @Column(name = "source_product_id") var sourceProductId: UUID = UUID.randomUUID(),
    @Column(name = "target_product_id") var targetProductId: UUID = UUID.randomUUID(),
    @Enumerated(EnumType.STRING) @Column(nullable = false) var status: CatalogMergeStatus = CatalogMergeStatus.PENDING,
    @Column(name = "created_by_user_id") var createdByUserId: Long? = null,
    @Column(name = "reviewed_by_user_id") var reviewedByUserId: Long? = null,
    @CreationTimestamp @Column(name = "created_at", updatable = false) var createdAt: OffsetDateTime? = null,
)

enum class LegacyCrosswalkStatus { PENDING, APPROVED, REJECTED }

@Entity
@Table(name = "legacy_product_crosswalk")
class LegacyProductCrosswalkEntity(
    @Id @Column(name = "shop_product_id") var shopProductId: Long = 0,
    @Column(name = "catalog_product_id", nullable = false) var catalogProductId: Long = 0,
    @Column(name = "catalog_variant_id") var catalogVariantId: Long? = null,
    @Enumerated(EnumType.STRING) @Column(nullable = false) var status: LegacyCrosswalkStatus = LegacyCrosswalkStatus.PENDING,
    @Column(name = "approved_by_user_id") var approvedByUserId: Long? = null,
    @Column(name = "approved_at") var approvedAt: OffsetDateTime? = null,
    @Column(columnDefinition = "text") var note: String? = null,
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) var createdAt: OffsetDateTime? = null,
)
