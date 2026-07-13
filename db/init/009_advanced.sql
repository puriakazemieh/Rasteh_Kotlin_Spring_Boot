-- ====== Advanced: community / subscription / notifications (idempotent) ======

CREATE TABLE IF NOT EXISTS community_posts (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    author_name   VARCHAR(120),
    body          TEXT NOT NULL,
    comment_count INT NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS community_comments (
    id          BIGSERIAL PRIMARY KEY,
    post_id     BIGINT NOT NULL REFERENCES community_posts(id) ON DELETE CASCADE,
    user_id     BIGINT NOT NULL,
    author_name VARCHAR(120),
    body        TEXT NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_comments_post ON community_comments(post_id);

CREATE TABLE IF NOT EXISTS subscriptions (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    plan       VARCHAR(20) NOT NULL DEFAULT 'PLUS',
    active     BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMPTZ,
    CONSTRAINT uq_subscription_user UNIQUE (user_id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    title      VARCHAR(160) NOT NULL,
    body       TEXT,
    read       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(user_id);
