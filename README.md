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

فازهای بعدی (کاتالوگِ دوحالته، چت/پیشنهاد/بوکمارک، سبد/سفارش + rastehSearch، ۲۰ قابلیت) در `RASTEH_SERVER_PLAN.md`.

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
