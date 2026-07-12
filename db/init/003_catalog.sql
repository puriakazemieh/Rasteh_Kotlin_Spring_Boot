-- ====== Marketplace catalog: shop products (v2, idempotent) ======
-- کالا/آگهیِ هر فروشگاه. purchasable سمتِ سرور مشتق می‌شود (BUYABLE + موجودی).
-- با ddl-auto=update هایبرنیت هم این جدول را از روی Entity می‌سازد؛ این فایل برای init کانتینرِ Postgres است.

CREATE TABLE IF NOT EXISTS shop_products (
    id               BIGSERIAL PRIMARY KEY,
    shop_id          BIGINT NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    name             VARCHAR(180) NOT NULL,
    description      TEXT,
    price            NUMERIC(15,0) NOT NULL DEFAULT 0,
    old_price        NUMERIC(15,0),
    discount_percent INT,
    condition        VARCHAR(20) NOT NULL DEFAULT 'NEW',   -- NEW / USED / REFURBISHED
    stock            INT NOT NULL DEFAULT 0,
    category_name    VARCHAR(120),
    emoji            VARCHAR(16),
    image_url        TEXT,
    active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_shop_products_shop ON shop_products(shop_id);
CREATE INDEX IF NOT EXISTS idx_shop_products_shop_active ON shop_products(shop_id, active);
