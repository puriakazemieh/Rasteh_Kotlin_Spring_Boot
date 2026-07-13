-- ====== Services wave: appointment / giftcard / return (idempotent) ======

CREATE TABLE IF NOT EXISTS appointments (
    id           BIGSERIAL PRIMARY KEY,
    shop_id      BIGINT NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    user_id      BIGINT NOT NULL,
    scheduled_at TIMESTAMPTZ NOT NULL,
    note         TEXT,
    status       VARCHAR(20) NOT NULL DEFAULT 'REQUESTED',
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_appointments_user ON appointments(user_id);
CREATE INDEX IF NOT EXISTS idx_appointments_shop ON appointments(shop_id);

CREATE TABLE IF NOT EXISTS gift_cards (
    id             BIGSERIAL PRIMARY KEY,
    code           VARCHAR(20) NOT NULL,
    initial_amount NUMERIC(15,0) NOT NULL DEFAULT 0,
    balance        NUMERIC(15,0) NOT NULL DEFAULT 0,
    owner_user_id  BIGINT,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_giftcard_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS return_requests (
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    reason     TEXT,
    status     VARCHAR(20) NOT NULL DEFAULT 'REQUESTED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_returns_user ON return_requests(user_id);
