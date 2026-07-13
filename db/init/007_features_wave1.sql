-- ====== Features wave 1: flash / groupbuy / loyalty / pricealert (idempotent) ======

CREATE TABLE IF NOT EXISTS flash_sales (
    id               BIGSERIAL PRIMARY KEY,
    product_id       BIGINT NOT NULL REFERENCES shop_products(id) ON DELETE CASCADE,
    discount_percent INT NOT NULL DEFAULT 0,
    starts_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ends_at          TIMESTAMPTZ NOT NULL,
    stock_limit      INT NOT NULL DEFAULT 0,
    sold_count       INT NOT NULL DEFAULT 0,
    active           BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX IF NOT EXISTS idx_flash_active ON flash_sales(active);

CREATE TABLE IF NOT EXISTS group_buys (
    id            BIGSERIAL PRIMARY KEY,
    product_id    BIGINT NOT NULL REFERENCES shop_products(id) ON DELETE CASCADE,
    unit_price    NUMERIC(15,0) NOT NULL DEFAULT 0,
    target_count  INT NOT NULL DEFAULT 2,
    current_count INT NOT NULL DEFAULT 0,
    ends_at       TIMESTAMPTZ NOT NULL,
    active        BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX IF NOT EXISTS idx_groupbuy_active ON group_buys(active);

CREATE TABLE IF NOT EXISTS group_buy_participants (
    id           BIGSERIAL PRIMARY KEY,
    group_buy_id BIGINT NOT NULL REFERENCES group_buys(id) ON DELETE CASCADE,
    user_id      BIGINT NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_groupbuy_user UNIQUE (group_buy_id, user_id)
);

CREATE TABLE IF NOT EXISTS loyalty_accounts (
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    points  INT NOT NULL DEFAULT 0,
    tier    VARCHAR(20) NOT NULL DEFAULT 'BRONZE',
    CONSTRAINT uq_loyalty_user UNIQUE (user_id)
);

CREATE TABLE IF NOT EXISTS price_alerts (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    product_id   BIGINT NOT NULL REFERENCES shop_products(id) ON DELETE CASCADE,
    target_price NUMERIC(15,0) NOT NULL DEFAULT 0,
    active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_price_alerts_user ON price_alerts(user_id);
