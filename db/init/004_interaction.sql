-- ====== Interaction: chat / offer / bookmark (v2, idempotent) ======
-- گفت‌وگویِ خریدار↔فروشگاه، پیام‌ها، پیشنهادِ قیمت، و نشان‌کردن (جایگزینِ فالو).
-- با ddl-auto=update هایبرنیت هم این جداول را می‌سازد؛ این فایل برای init کانتینرِ Postgres است.

CREATE TABLE IF NOT EXISTS conversations (
    id                   BIGSERIAL PRIMARY KEY,
    customer_user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    shop_id              BIGINT NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_message_at      TIMESTAMPTZ,
    last_message_preview VARCHAR(200),
    CONSTRAINT uq_conversation_customer_shop UNIQUE (customer_user_id, shop_id)
);

CREATE INDEX IF NOT EXISTS idx_conversations_customer ON conversations(customer_user_id);
CREATE INDEX IF NOT EXISTS idx_conversations_shop ON conversations(shop_id);

CREATE TABLE IF NOT EXISTS messages (
    id               BIGSERIAL PRIMARY KEY,
    conversation_id  BIGINT NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender_user_id   BIGINT NOT NULL,
    body             TEXT NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_messages_conversation ON messages(conversation_id);

CREATE TABLE IF NOT EXISTS offers (
    id               BIGSERIAL PRIMARY KEY,
    shop_id          BIGINT NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    product_id       BIGINT REFERENCES shop_products(id) ON DELETE SET NULL,
    customer_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount           NUMERIC(15,0) NOT NULL DEFAULT 0,
    message          TEXT,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',   -- PENDING / ACCEPTED / REJECTED
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_offers_shop ON offers(shop_id);
CREATE INDEX IF NOT EXISTS idx_offers_customer ON offers(customer_user_id);

CREATE TABLE IF NOT EXISTS bookmarks (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    shop_id     BIGINT REFERENCES shops(id) ON DELETE CASCADE,
    product_id  BIGINT REFERENCES shop_products(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_bookmarks_user ON bookmarks(user_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_bookmark_user_shop ON bookmarks(user_id, shop_id) WHERE shop_id IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_bookmark_user_product ON bookmarks(user_id, product_id) WHERE product_id IS NOT NULL;
