-- ====== Marketplace: Rasteh × Location × Shop (v2, idempotent) ======
-- شهر / راسته(صنف) / محل(پاساژ‌بازار) / نگاشتِ راسته‌محل / فروشگاه / حالتِ دستهٔ فروشگاه.
-- با ddl-auto=update هایبرنیت هم این جداول را از روی Entityها می‌سازد؛ این فایل برای init کانتینرِ Postgres است.

CREATE TABLE IF NOT EXISTS cities (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    province    VARCHAR(120),
    is_active   BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS rastehs (
    id          BIGSERIAL PRIMARY KEY,
    label       VARCHAR(120) NOT NULL,
    color_oklch VARCHAR(60),
    icon_key    VARCHAR(60),
    sort_order  INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS locations (
    id            BIGSERIAL PRIMARY KEY,
    city_id       BIGINT REFERENCES cities(id) ON DELETE SET NULL,
    name          VARCHAR(180) NOT NULL,
    kind          VARCHAR(20) NOT NULL DEFAULT 'PASSAGE',   -- PASSAGE / BAZAAR / STREET
    address       VARCHAR(255),
    floor_count   INT NOT NULL DEFAULT 1,
    map_image_url TEXT,
    lat           DOUBLE PRECISION,
    lng           DOUBLE PRECISION
);

CREATE INDEX IF NOT EXISTS idx_locations_city ON locations(city_id);

-- نگاشتِ چند-به-چندِ راسته ↔ محل (باتم‌شیتِ انتخابِ محل)
CREATE TABLE IF NOT EXISTS rasteh_locations (
    rasteh_id   BIGINT NOT NULL REFERENCES rastehs(id)   ON DELETE CASCADE,
    location_id BIGINT NOT NULL REFERENCES locations(id) ON DELETE CASCADE,
    PRIMARY KEY (rasteh_id, location_id)
);

CREATE TABLE IF NOT EXISTS shops (
    id                 BIGSERIAL PRIMARY KEY,
    location_id        BIGINT REFERENCES locations(id) ON DELETE SET NULL,
    rasteh_id          BIGINT REFERENCES rastehs(id)   ON DELETE SET NULL,
    owner_user_id      BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    name               VARCHAR(180) NOT NULL,
    category           VARCHAR(120),
    floor              VARCHAR(40),
    type               VARCHAR(20) NOT NULL DEFAULT 'BUYABLE',   -- BUYABLE / VISIT_ONLY
    verified           BOOLEAN NOT NULL DEFAULT FALSE,
    rating             NUMERIC(3,2) NOT NULL DEFAULT 0,
    reviews_count      INT NOT NULL DEFAULT 0,
    sales_count        INT NOT NULL DEFAULT 0,
    phone              VARCHAR(30),
    has_chat           BOOLEAN NOT NULL DEFAULT TRUE,
    accepts_offers     BOOLEAN NOT NULL DEFAULT FALSE,
    about              TEXT,
    working_hours_json TEXT,
    address            VARCHAR(255),
    map_x              DOUBLE PRECISION,
    map_y              DOUBLE PRECISION,
    emoji              VARCHAR(16),
    cover_style        VARCHAR(120),
    cover_url          TEXT,
    logo_url           TEXT,
    status             VARCHAR(20) NOT NULL DEFAULT 'PENDING',   -- PENDING / APPROVED / REJECTED / SUSPENDED
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    approved_at        TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_shops_location_status ON shops(location_id, status);
CREATE INDEX IF NOT EXISTS idx_shops_rasteh ON shops(rasteh_id);
CREATE INDEX IF NOT EXISTS idx_shops_owner ON shops(owner_user_id);

CREATE TABLE IF NOT EXISTS vendor_category_modes (
    id           BIGSERIAL PRIMARY KEY,
    shop_id      BIGINT NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    category_id  BIGINT,
    mode         VARCHAR(20) NOT NULL DEFAULT 'COMMERCE',    -- SHOWCASE / COMMERCE
    CONSTRAINT uq_shop_category UNIQUE (shop_id, category_id)
);
