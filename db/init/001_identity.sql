-- ====== Identity (idempotent) ======
-- با ddl-auto=update هایبرنیت هم این جداول را از روی Entityها می‌سازد؛
-- این فایل برای راه‌اندازیِ اولیهٔ کانتینرِ Postgres (docker-entrypoint-initdb.d) است.

CREATE TABLE IF NOT EXISTS users (
    id                          BIGSERIAL PRIMARY KEY,
    email                       VARCHAR(255) UNIQUE,
    password_hash               VARCHAR(255) NOT NULL,
    first_name                  VARCHAR(50),
    last_name                   VARCHAR(50),
    city                        VARCHAR(50),
    postal_code                 INTEGER,
    phone                       VARCHAR(30) UNIQUE,
    role                        VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER', -- CUSTOMER / VENDOR / ADMIN
    is_active                   BOOLEAN NOT NULL DEFAULT TRUE,
    reset_password_token        VARCHAR(255),
    reset_password_token_expiry TIMESTAMPTZ,
    otp_code                    VARCHAR(255),
    otp_expiry                  TIMESTAMPTZ,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token_hash CHAR(64)    NOT NULL UNIQUE, -- SHA-256 hex
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user ON refresh_tokens (user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_active ON refresh_tokens (user_id) WHERE revoked_at IS NULL;
