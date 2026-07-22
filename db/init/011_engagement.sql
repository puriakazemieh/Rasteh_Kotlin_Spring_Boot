-- ====== Engagement wave: events / referrals (idempotent) ======

CREATE TABLE IF NOT EXISTS events (
    id           BIGSERIAL PRIMARY KEY,
    location_id  BIGINT,
    title        VARCHAR(160) NOT NULL,
    description  TEXT,
    event_date   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_events_active_date ON events(active, event_date);
CREATE INDEX IF NOT EXISTS idx_events_location ON events(location_id);

CREATE TABLE IF NOT EXISTS referrals (
    id              BIGSERIAL PRIMARY KEY,
    inviter_user_id BIGINT NOT NULL,
    code            VARCHAR(16) NOT NULL,
    invitee_user_id BIGINT,
    reward_status   VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_referrals_inviter ON referrals(inviter_user_id);
CREATE INDEX IF NOT EXISTS idx_referrals_code ON referrals(code);
CREATE INDEX IF NOT EXISTS idx_referrals_invitee ON referrals(invitee_user_id);

CREATE TABLE IF NOT EXISTS warranties (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    title       VARCHAR(160) NOT NULL,
    serial      VARCHAR(80),
    valid_until TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_warranties_user ON warranties(user_id);

CREATE TABLE IF NOT EXISTS parking_sessions (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    spot       VARCHAR(40) NOT NULL,
    entered_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    exited_at  TIMESTAMPTZ,
    fee        NUMERIC(15,0) NOT NULL DEFAULT 0,
    paid       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_parking_user ON parking_sessions(user_id);

CREATE TABLE IF NOT EXISTS live_sessions (
    id                BIGSERIAL PRIMARY KEY,
    shop_id           BIGINT,
    shop_name         VARCHAR(120),
    title             VARCHAR(160) NOT NULL,
    status            VARCHAR(20) NOT NULL DEFAULT 'LIVE',
    pinned_product_id BIGINT,
    viewer_count      INT NOT NULL DEFAULT 0,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_live_status ON live_sessions(status);

CREATE TABLE IF NOT EXISTS escrows (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    order_id    BIGINT,
    amount      NUMERIC(15,0) NOT NULL DEFAULT 0,
    status      VARCHAR(20) NOT NULL DEFAULT 'HELD',
    released_at TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_escrows_user ON escrows(user_id);
