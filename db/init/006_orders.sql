-- ====== Marketplace orders: single-vendor (v2, idempotent) ======
-- سفارشِ تک‌ونـدوری + اقلام. کسرِ موجودی و مبلغ سمتِ سرور انجام می‌شود.

CREATE TABLE IF NOT EXISTS marketplace_orders (
    id               BIGSERIAL PRIMARY KEY,
    shop_id          BIGINT NOT NULL REFERENCES shops(id) ON DELETE RESTRICT,
    customer_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',   -- PENDING/CONFIRMED/PREPARING/READY/COMPLETED/CANCELLED
    total_amount     NUMERIC(15,0) NOT NULL DEFAULT 0,
    note             TEXT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_mp_orders_customer ON marketplace_orders(customer_user_id);
CREATE INDEX IF NOT EXISTS idx_mp_orders_shop ON marketplace_orders(shop_id);

CREATE TABLE IF NOT EXISTS marketplace_order_items (
    id           BIGSERIAL PRIMARY KEY,
    order_id     BIGINT NOT NULL REFERENCES marketplace_orders(id) ON DELETE CASCADE,
    product_id   BIGINT REFERENCES shop_products(id) ON DELETE SET NULL,
    product_name VARCHAR(200) NOT NULL,
    unit_price   NUMERIC(15,0) NOT NULL DEFAULT 0,
    quantity     INT NOT NULL DEFAULT 1,
    line_total   NUMERIC(15,0) NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_mp_order_items_order ON marketplace_order_items(order_id);
