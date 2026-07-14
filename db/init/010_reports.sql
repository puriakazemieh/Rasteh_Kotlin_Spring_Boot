-- ====== Reports (abuse reports on shop/product) — idempotent ======
CREATE TABLE IF NOT EXISTS reports (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    target_type VARCHAR(20) NOT NULL DEFAULT 'SHOP',   -- SHOP / PRODUCT
    target_id   BIGINT NOT NULL,
    reason      TEXT,
    status      VARCHAR(20) NOT NULL DEFAULT 'OPEN',   -- OPEN / RESOLVED / DISMISSED
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_reports_status ON reports(status);
