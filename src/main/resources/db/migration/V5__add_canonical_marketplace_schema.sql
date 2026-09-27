-- Canonical marketplace schema. This migration is expand-only: legacy tables
-- remain the source for existing flows during the compatibility window.

CREATE TABLE merchant_organizations (
    id UUID PRIMARY KEY,
    external_shop_id BIGINT NOT NULL UNIQUE REFERENCES shops(id),
    owner_user_id BIGINT NOT NULL REFERENCES users(id),
    display_name VARCHAR(180) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE shop_locations_v1 (
    id UUID PRIMARY KEY,
    merchant_organization_id UUID NOT NULL UNIQUE REFERENCES merchant_organizations(id),
    external_shop_id BIGINT NOT NULL UNIQUE REFERENCES shops(id),
    external_location_id BIGINT REFERENCES locations(id),
    name VARCHAR(180) NOT NULL,
    address TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE canonical_products (
    id UUID PRIMARY KEY,
    external_catalog_product_id BIGINT UNIQUE REFERENCES products(id),
    external_shop_product_id BIGINT UNIQUE REFERENCES shop_products(id),
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(280),
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CHECK (
        (external_catalog_product_id IS NOT NULL)::INT +
        (external_shop_product_id IS NOT NULL)::INT = 1
    )
);

CREATE TABLE canonical_variants (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES canonical_products(id),
    external_catalog_variant_id BIGINT UNIQUE REFERENCES product_variants(id),
    sku VARCHAR(80),
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE shop_offers (
    id UUID PRIMARY KEY,
    shop_location_id UUID NOT NULL REFERENCES shop_locations_v1(id),
    product_id UUID NOT NULL REFERENCES canonical_products(id),
    variant_id UUID REFERENCES canonical_variants(id),
    external_shop_product_id BIGINT UNIQUE REFERENCES shop_products(id),
    price_minor_units BIGINT NOT NULL CHECK (price_minor_units >= 0),
    currency VARCHAR(3) NOT NULL DEFAULT 'IRR' CHECK (currency = 'IRR'),
    sale_mode VARCHAR(16) NOT NULL CHECK (sale_mode IN ('SHOWCASE', 'RESERVABLE', 'BUYABLE')),
    on_hand INTEGER NOT NULL DEFAULT 0 CHECK (on_hand >= 0),
    reserved INTEGER NOT NULL DEFAULT 0 CHECK (reserved >= 0 AND reserved <= on_hand),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_shop_offers_location_active ON shop_offers(shop_location_id, active);
CREATE INDEX idx_shop_offers_product_active ON shop_offers(product_id, active);

-- No inferred row is inserted here. An approved mapping is the only way to
-- merge an existing marketplace product with a catalog product/variant.
CREATE TABLE legacy_product_crosswalk (
    shop_product_id BIGINT PRIMARY KEY REFERENCES shop_products(id),
    catalog_product_id BIGINT NOT NULL REFERENCES products(id),
    catalog_variant_id BIGINT REFERENCES product_variants(id),
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    approved_by_user_id BIGINT REFERENCES users(id),
    approved_at TIMESTAMPTZ,
    note TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CHECK (
        (status = 'APPROVED' AND approved_by_user_id IS NOT NULL AND approved_at IS NOT NULL) OR
        status <> 'APPROVED'
    )
);

CREATE INDEX idx_legacy_product_crosswalk_catalog_product ON legacy_product_crosswalk(catalog_product_id);
