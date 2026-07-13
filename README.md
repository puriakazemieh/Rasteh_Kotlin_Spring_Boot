# Rasteh — سرور (Kotlin + Spring Boot)

بک‌اندِ مارکت‌پلیسِ محلیِ پاساژ/راسته. نقشهٔ راه در [`RASTEH_SERVER_PLAN.md`](./RASTEH_SERVER_PLAN.md)
و مرجعِ طراحی در پوشهٔ [`design_handoff_unified_app/`](./design_handoff_unified_app) است.

## وضعیت: فازِ ۰ + فازِ ۱

**فازِ ۰ (اسکلت + احراز هویت):**
- اسکلتِ Gradle/Spring Boot + Docker Compose برای Postgres.
- ماژول‌های `identity` (auth/JWT، نقش‌های `CUSTOMER/VENDOR/ADMIN/SUPERADMIN`) و `shared`.
- پاریتیِ کاملِ ماژول‌هایِ فروشگاه (catalog/order/cart/…) به‌عنوانِ پایهٔ بازاستفاده.

**فازِ ۱ (راسته/محل + Vendor onboarding + تأییدِ ادمین) — ماژولِ `marketplace`:**
- موجودیت‌ها: `City`, `Rasteh`(صنف), `Location`(محل, نگاشتِ چند-به-چندِ راسته↔محل), `Shop`, `VendorCategoryMode`.
- endpointها:
  - `GET /api/rastehs` — گریدِ راسته‌هایِ خانه (با رنگ/آیکون).
  - `GET /api/rastehs/{id}/locations` — محل‌هایِ یک راسته (باتم‌شیت).
  - `GET /api/locations/{id}` · `GET /api/cities?query=` — محل/انتخابِ شهر.
  - `POST /api/shops` — ثبتِ درخواستِ فروشگاه (becomeVendor؛ وضعیتِ PENDING). `GET /api/shops/mine` · `GET /api/shops/{id}`.
  - `GET/POST /api/admin/shops` + `.../{id}/approve|reject|suspend` — صفِ تأیید (ADMIN/SUPERADMIN). تأیید → نقشِ VENDOR به مالک اعطا می‌شود.
- migration `002_marketplace.sql` بازنویسی شد به مدلِ v2؛ `MarketplaceSeeder` راسته‌ها/محل‌های نمونه را می‌سازد.
- `DataSeeder` سه حسابِ نقش‌محورِ نمونه می‌سازد (رمزِ همه `pass1234`): ادمین `09120000000` · فروشنده `09122222222` · خریدار `09121111111`.

**فازِ ۲ (کاتالوگِ دوحالتهٔ فروشگاه‌محور) — `marketplace` گسترش یافت:**
- موجودیتِ `ShopProduct` (نام، قیمت، قیمتِ قبلی، درصدِ تخفیف، وضعیت new/used، موجودی، دسته، اموجی، تصویر، فعال).
  قابلِ خرید بودن (`purchasable`) سمتِ سرور مشتق می‌شود: `type=BUYABLE` + موجودی>۰.
- endpointها:
  - عمومی: `GET /api/products?shopId=` · `GET /api/products/{id}` — کاتالوگِ فروشگاه.
  - عمومی: `GET /api/locations/{id}/shops?rastehId=` — فهرستِ فروشگاه‌هایِ محل (**rastehSearch**).
  - ونـدور (احرازشده، مالک یا ادمین): `GET /api/vendor/products/shop/{shopId}` · `POST /api/vendor/products`
    · `PUT /api/vendor/products/{id}` · `DELETE /api/vendor/products/{id}` — **manageListings/addProduct**.
- migration `003_catalog.sql` (جدولِ `shop_products`)؛ `MarketplaceShopSeeder` سه فروشگاهِ تأییدشدهٔ نمونه
  با کالا می‌سازد (فروشندهٔ نمونه: `09122222222` / رمز `vendor1234`).

**فازِ ۳ (چت + پیشنهادِ قیمت + بوکمارک) — ماژولِ `interaction`:**
- **چت** (`Conversation`/`Message`): `POST /api/chat/conversations` (ساخت/گرفتن) · `GET /api/chat/conversations`
  (خریدار+فروشنده) · `GET|POST /api/chat/conversations/{id}/messages` — «پیام به فروشگاه» (polling).
- **پیشنهادِ قیمت** (`Offer`): `POST /api/offers` (فقط اگر `acceptsOffers`) · `GET /api/offers/mine`
  · `GET /api/offers/shop/{shopId}` · `POST /api/offers/{id}/accept|reject` — makeOffer.
- **بوکمارک** (`Bookmark`، جایگزینِ فالو): `GET /api/bookmarks` · `POST /api/bookmarks` (فروشگاه/کالا)
  · `DELETE /api/bookmarks/{id}`.
- RBAC: شرکت‌کنندهٔ گفت‌وگو (خریدار/فروشنده/ادمین)؛ پیشنهاد فقط برایِ مالکِ فروشگاه قابلِ پذیرش/رد.
- migration `004_interaction.sql`.

**فازِ ۴a (نظرات + جست‌وجو):**
- **نظرات** (`Review`): `GET /api/reviews?shopId=` · `POST /api/reviews` (یک نظر به‌ازای هر کاربر؛
  میانگین/شمارش در `shops.rating/reviews_count` بازمحاسبه می‌شود) — تبِ نظراتِ shopDetail.
- **جست‌وجو** (`Specification`): `GET /api/search/shops?query=&rastehId=&locationId=&type=&sort=`
  و `GET /api/search/products?query=&shopId=&condition=&minPrice=&maxPrice=&sort=` — صفحهٔ search.
- migration `005_reviews.sql`.

**فازِ ۴b (سفارشِ تک‌ونـدوری) — `marketplace/order`:**
- موجودیت‌های `MarketplaceOrder`/`MarketplaceOrderItem`؛ سبد سمتِ کلاینت، سفارش سمتِ سرور.
- `POST /api/orders` (checkout: اعتبارسنجی، تک‌ونـدوری، کسرِ موجودی، محاسبهٔ مبلغ)
  · `GET /api/orders/mine` · `GET /api/orders/{id}` · `POST /api/orders/{id}/status`
  · `GET /api/vendor/orders/shop/{shopId}` (vendorOrders).
- migration `006_orders.sql`.

**فازِ ۵ (قابلیت‌های تجاریِ موجِ اول) — ماژولِ `features`:**
- **فلش** `GET /api/flash` (عمومی) · `POST /api/flash` (ونـدور/ادمین).
- **خریدِ گروهی** `GET /api/groupbuys` · `POST /api/groupbuys` · `POST /api/groupbuys/{id}/join`.
- **وفاداری** `GET /api/loyalty/me` (امتیاز/سطح).
- **هشدارِ قیمت** `GET /api/pricealerts/mine` · `POST /api/pricealerts` · `DELETE /api/pricealerts/{id}`.
- migration `007_features_wave1.sql`.

**فازِ ۶ (خدماتی) — ماژولِ `services`:**
- **رزروِ بازدید** `POST /api/appointments` · `GET /api/appointments/mine`.
- **کارتِ هدیه** `POST /api/giftcards` · `POST /api/giftcards/redeem` · `GET /api/giftcards/mine`.
- **مرجوعی** `POST /api/returns` · `GET /api/returns/mine`.
- migration `008_services.sql`.

**فازِ ۷+۸ (پیشرفته + صیقل) — ماژولِ `advanced`:**
- **انجمن** `GET/POST /api/community` · `GET/POST /api/community/{id}/comments`.
- **اشتراکِ پلاس** `GET /api/subscription/me` · `POST /api/subscription/subscribe`.
- **اعلان‌ها** `GET /api/notifications` · `POST /api/notifications/{id}/read`.
- **آنالیتیکسِ ونـدور** `GET /api/vendor/analytics/shop/{shopId}` (تعدادِ کالا/سفارش، درآمد، پیشنهادهای معلق).
- migration `009_advanced.sql`.

قابلیت‌های نیازمندِ زیرساختِ سنگین (live/escrow/concierge/visualsearch/parking) طبقِ پلن به‌عنوانِ «آینده» می‌مانند.

## اجرا (روی دستگاهِ محلی)

> توجه: در محیطِ ابری امکانِ build نیست (۴۰۳ هنگام دانلودِ توزیعِ Gradle). روی دستگاهِ خودتان اجرا کنید.

```bash
# ۱) بالا آوردنِ Postgres
docker compose up -d db

# ۲) اجرای اپ
./gradlew bootRun
```

- Swagger: `http://localhost:8080/api/swagger-ui.html`
- تنظیماتِ اتصال با متغیرهای محیطی قابلِ override است (`DATASOURCE_URL`, `JWT_SECRET_KEY`, …) — رجوع به `application.properties`.
- برای خاموش‌کردنِ داده‌ی نمونه: `SEED_ENABLED=false`.

## ساختار
```
src/main/kotlin/com/kazemieh/rasteh/
  identity/   احراز هویت (api / application / persistence / domain)
  shared/     امنیت، JWT، خطا، پیکربندی، ایمیل/پیامک
db/init/      migrationهای idempotent (docker-entrypoint-initdb.d)
```
