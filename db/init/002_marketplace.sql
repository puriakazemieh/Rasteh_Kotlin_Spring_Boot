-- ====== Marketplace base tables (Phase 0, idempotent) ======
-- شهر / پاساژ / فروشگاه(ونـدور) / حالتِ دسته‌ی فروشگاه.
-- موجودیت‌های JPA در فازِ ۱ اضافه می‌شوند؛ این فایل پایهٔ اسکیما را می‌سازد.

CREATE TABLE IF NOT EXISTS cities (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    province    VARCHAR(120),
    is_active   BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS malls (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(180) NOT NULL,
    city          VARCHAR(120),
    address       VARCHAR(255),
    floor_count   INT NOT NULL DEFAULT 1,
    description   TEXT,
    floor_labels  JSONB NOT NULL DEFAULT '[]'::jsonb,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS shops (
    id              BIGSERIAL PRIMARY KEY,
    mall_id         BIGINT REFERENCES malls(id) ON DELETE SET NULL,
    owner_user_id   BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    name            VARCHAR(180) NOT NULL,
    category        VARCHAR(120),
    floor           VARCHAR(40),
    type            VARCHAR(20) NOT NULL DEFAULT 'BUYABLE',   -- BUYABLE / VISIT_ONLY
    verified        BOOLEAN NOT NULL DEFAULT FALSE,
    rating          NUMERIC(3,2) NOT NULL DEFAULT 0,
    reviews_count   INT NOT NULL DEFAULT 0,
    followers_count INT NOT NULL DEFAULT 0,
    sales_count     INT NOT NULL DEFAULT 0,
    satisfaction    INT NOT NULL DEFAULT 0,
    performance     INT NOT NULL DEFAULT 0,
    phone           VARCHAR(30),
    has_chat        BOOLEAN NOT NULL DEFAULT TRUE,
    accepts_offers  BOOLEAN NOT NULL DEFAULT FALSE,
    about           TEXT,
    working_hours   JSONB,
    address         VARCHAR(255),
    map_x           NUMERIC(8,3),
    map_y           NUMERIC(8,3),
    emoji           VARCHAR(16),
    cover_url       TEXT,
    logo_url        TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',   -- PENDING / APPROVED / REJECTED / SUSPENDED
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    approved_at     TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_shops_mall_status ON shops(mall_id, status);
CREATE INDEX IF NOT EXISTS idx_shops_owner ON shops(owner_user_id);
CREATE INDEX IF NOT EXISTS idx_shops_category ON shops(category);

CREATE TABLE IF NOT EXISTS vendor_category_modes (
    id           BIGSERIAL PRIMARY KEY,
    shop_id      BIGINT NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    category_id  BIGINT,
    mode         VARCHAR(20) NOT NULL DEFAULT 'COMMERCE',    -- SHOWCASE / COMMERCE
    CONSTRAINT uq_shop_category UNIQUE (shop_id, category_id)
);
