-- ====== Marketplace reviews (v2, idempotent) ======
-- نظرِ کاربر روی فروشگاه؛ میانگین/شمارش در ستون‌های shops.rating/reviews_count بازمحاسبه می‌شود.

CREATE TABLE IF NOT EXISTS reviews (
    id          BIGSERIAL PRIMARY KEY,
    shop_id     BIGINT NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    user_id     BIGINT NOT NULL,
    author_name VARCHAR(120),
    rating      INT NOT NULL DEFAULT 5,
    comment     TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_review_user_shop UNIQUE (user_id, shop_id)
);

CREATE INDEX IF NOT EXISTS idx_reviews_shop ON reviews(shop_id);
