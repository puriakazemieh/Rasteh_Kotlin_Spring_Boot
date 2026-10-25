CREATE TABLE catalog_events (
    id UUID PRIMARY KEY,
    event_type VARCHAR(48) NOT NULL,
    offer_id UUID REFERENCES shop_offers(id),
    payload_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_catalog_events_offer_created ON catalog_events(offer_id, created_at DESC);
