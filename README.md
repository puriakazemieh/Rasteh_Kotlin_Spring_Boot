# Rasteh — سرور (Kotlin + Spring Boot)

بک‌اندِ مارکت‌پلیسِ محلیِ پاساژ/راسته. نقشهٔ راه در [`RASTEH_SERVER_PLAN.md`](./RASTEH_SERVER_PLAN.md)
و مرجعِ طراحی در پوشهٔ [`design_handoff_unified_app/`](./design_handoff_unified_app) است.

## وضعیت: فازِ ۰ (اسکلت + احراز هویت)

پیاده‌شده در این فاز:
- اسکلتِ Gradle/Spring Boot + Docker Compose برای Postgres.
- ماژولِ `identity` (کپیِ کاملِ auth/JWT از `shop-kotlin-spring-boot`) با نقشِ `VENDOR` افزوده‌شده به `UserRole`.
- ماژولِ `shared` (امنیت/JWT، مدیریتِ خطا، پیکربندی‌ها، سرویسِ ایمیل/پیامک).
- migrationهای پایه: `001_identity.sql` و `002_marketplace.sql` (جدول‌های `cities/malls/shops/vendor_category_modes`).
- `DataSeeder` سه حسابِ نقش‌محورِ نمونه می‌سازد (رمزِ همه `pass1234`):
  - ادمین `09120000000` · فروشنده `09122222222` · خریدار `09121111111`

فازهای بعدی (Vendor onboarding، کاتالوگِ دوحالته، دنبال‌کردن/چت/پیشنهاد، سبد/سفارش، ۲۰ قابلیت) در پلن آمده‌اند.

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
