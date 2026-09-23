CREATE TABLE IF NOT EXISTS addresses (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    receiver_name  VARCHAR(255) NOT NULL,
    receiver_phone VARCHAR(30) NOT NULL,
    country        VARCHAR(80) NOT NULL DEFAULT 'IR',
    province       VARCHAR(120) NOT NULL,
    city           VARCHAR(120) NOT NULL,
    address_line1  VARCHAR(255) NOT NULL,
    address_line2  VARCHAR(255),
    postal_code    VARCHAR(20),
    is_default     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_addresses_user ON addresses(user_id);
