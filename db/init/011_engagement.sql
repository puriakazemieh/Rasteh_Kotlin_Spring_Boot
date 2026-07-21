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
