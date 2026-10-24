-- Phase 07: exact-money offers, inventory ledger and freshness.
ALTER TABLE shop_offers ALTER COLUMN price_minor_units DROP NOT NULL;
ALTER TABLE shop_offers ADD COLUMN IF NOT EXISTS fulfillment_json JSONB NOT NULL DEFAULT '[]'::jsonb;
ALTER TABLE shop_offers ADD COLUMN IF NOT EXISTS last_confirmed_at TIMESTAMPTZ;
ALTER TABLE shop_offers ADD COLUMN IF NOT EXISTS expires_at TIMESTAMPTZ;
ALTER TABLE shop_offers ADD COLUMN IF NOT EXISTS inventory_source VARCHAR(32) NOT NULL DEFAULT 'MANUAL';
ALTER TABLE shop_offers ADD COLUMN IF NOT EXISTS stale BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE shop_offers ADD CONSTRAINT chk_offer_mode_invariants CHECK (
 (sale_mode = 'SHOWCASE' AND price_minor_units IS NULL) OR
 (sale_mode = 'RESERVABLE' AND (price_minor_units IS NULL OR price_minor_units >= 0)) OR
 (sale_mode = 'BUYABLE' AND price_minor_units IS NOT NULL AND price_minor_units >= 0 AND on_hand >= reserved)
);
CREATE INDEX idx_shop_offers_freshness ON shop_offers(active, expires_at);
CREATE TABLE inventory_ledger (id UUID PRIMARY KEY, offer_id UUID NOT NULL REFERENCES shop_offers(id), delta_on_hand INTEGER NOT NULL DEFAULT 0, delta_reserved INTEGER NOT NULL DEFAULT 0, reason VARCHAR(40) NOT NULL, idempotency_key VARCHAR(120) NOT NULL, actor_user_id BIGINT REFERENCES users(id), created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), UNIQUE(offer_id, idempotency_key), CHECK(delta_on_hand <> 0 OR delta_reserved <> 0));
CREATE TABLE offer_reservations (id UUID PRIMARY KEY, offer_id UUID NOT NULL REFERENCES shop_offers(id), quantity INTEGER NOT NULL CHECK(quantity > 0), status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE', expires_at TIMESTAMPTZ NOT NULL, idempotency_key VARCHAR(120) NOT NULL UNIQUE, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW());
CREATE INDEX idx_offer_reservations_expiry ON offer_reservations(status, expires_at);
CREATE TABLE offer_media (id UUID PRIMARY KEY, offer_id UUID NOT NULL REFERENCES shop_offers(id), object_key VARCHAR(255) NOT NULL, media_type VARCHAR(100) NOT NULL, position INTEGER NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), UNIQUE(offer_id, object_key));
CREATE TABLE catalog_merge_queue (id UUID PRIMARY KEY, source_product_id UUID NOT NULL REFERENCES canonical_products(id), target_product_id UUID NOT NULL REFERENCES canonical_products(id), status VARCHAR(16) NOT NULL DEFAULT 'PENDING', created_by_user_id BIGINT REFERENCES users(id), reviewed_by_user_id BIGINT REFERENCES users(id), created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), CHECK(source_product_id <> target_product_id));
