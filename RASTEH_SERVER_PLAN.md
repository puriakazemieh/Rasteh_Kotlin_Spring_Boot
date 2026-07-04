# پلنِ ساخت — Rasteh_Kotlin_Spring_Boot (سرور)

> مارکت‌پلیسِ محلی برایِ پاساژ/راسته: چند فروشگاهِ مستقل زیرِ یک چتر، هرکدوم با پروفایلِ خودش،
> که می‌توانند محصولاتشان را به‌صورتِ «فقط نمایشی» (Showcase) یا «کامل قابلِ‌خرید» (Commerce) عرضه کنند.

این سند خروجیِ یک تحلیلِ اورلپ با پروژه‌ی خواهرِ تک‌مستأجری (`shop-kotlin-spring-boot`) است.
حدودِ ۴۰-۵۰٪ زیرساخت (auth، پرداخت، تخفیف، فایل/تصویر) قابلِ کپیِ مستقیم است؛ کاتالوگ و سفارش
نیازِ ریفکتورِ vendor-scoping دارند؛ Vendor/تاییدِ ونـدور/Showcase-Commerce/نقشه‌ی طبقات کاملاً نو هستند.

---

## ۱) تصمیمِ تکنولوژی (فرق با پیشنهادِ اولیه‌ی کاربر)

پیشنهادِ اولیه Android+Compose تنها با Firebase بود. به‌جایِ آن **همان استکِ اثبات‌شده‌ی پروژه‌ی
فروشگاه** را ادامه می‌دهیم چون کدِ زیادی از آن قابلِ بازاستفاده است و تیم از قبل با آن آشناست:

- Kotlin + Spring Boot (Web MVC یا WebFlux — هرکدام که در `shop-kotlin-spring-boot` هست، همان)
- PostgreSQL + Hibernate/JPA، migration با فایل‌هایِ idempotent در `db/init/NNN_*.sql`
- ساختارِ ماژولی به سبکِ Clean Architecture: `api` (controller/dto/mapper) → `application` (service) → `persistence` (entity/repository)
- JWT auth (کپی از `shop/identity`)
- پرداخت: Zarinpal/IDPay از طریقِ همان `shop/payment` (کپی مستقیم)

---

## ۲) نقش‌ها

| نقش | معادل در پروژه‌ی فروشگاه | تفاوت |
|---|---|---|
| Customer | `customer` | همان، به‌علاوه‌یِ جست‌وجویِ چندونـدوری |
| Vendor (صاحبِ فروشگاه) | نزدیک‌ترین: `clinic/TherapistEntity` (ارائه‌دهنده‌ی خدمتی که ادمین تاییدش می‌کند) | ولی Vendor علاوه‌براین صاحبِ کاتالوگِ خودش هم هست |
| Admin پاساژ | `admin` | به‌علاوه‌یِ فرایندِ تاییدِ Vendor |

---

## ۳) مدلِ داده (Entityهایِ اصلی)

### جدید — از صفر

```
Mall                 (id, name, address, floorCount, description)
Vendor               (id, mallId, ownerUserId, name, logoUrl, coverUrl,
                       floorNumber, unitNumber, mapX, mapY,   -- موقعیت روی نقشه‌ی داخلی
                       primaryCategory, phone, workingHoursJson,
                       description, status: PENDING|APPROVED|REJECTED|SUSPENDED,
                       createdAt, approvedAt)
VendorCategoryMode   (vendorId, categoryId, mode: SHOWCASE|COMMERCE)   -- انتخاب per دسته، نه فقط per فروشگاه
```

### اقتباسی — کپی از `shop/catalog` با اضافه‌شدنِ `vendorId`

```
ProductEntity        += vendorId (FK -> Vendor), isPurchasable (Boolean, از VendorCategoryMode مشتق/کش می‌شود)
CategoryEntity       += vendorId (nullable اگر دسته‌بندیِ سراسری/مشترک هم بخواهیم)
ProductImageEntity, ProductVideoEntity, OptionType/OptionValue, InventoryEntity   -- بدون تغییرِ ساختاری، فقط از طریقِ product.vendorId قابلِ اسکوپ هستند
```

### اقتباسی — کپی از `shop/order` با split per-vendor

```
OrderEntity          += (بدون تغییر ساختاری اگر سبد تک‌ونـدوری باشد — رجوع به بخشِ ۴)
OrderItemEntity      += vendorId (برایِ گزارش‌گیریِ جداگانه حتی اگر سفارش تکی بماند)
```

### بدون تغییر — کپیِ مستقیم

```
UserEntity, RoleEntity   (شاید یک enum مقدار VENDOR اضافه شود)
DiscountEntity, CampaignEntity
PaymentTransactionEntity
```

---

## ۴) تصمیمِ کلیدیِ معماری: سبدِ خرید تک‌ونـدوری یا چندونـدوری؟

**پیشنهاد برایِ MVP: سبدِ خرید تک‌ونـدوری** (مثلِ اسنپ‌فود/دیجی‌کالا-پلاس در حالتِ pickup).
دلیل: چک‌اوتِ چندونـدوری (split کردنِ یک پرداخت بینِ چند فروشگاه، ارسال‌های جدا، لغوِ جزئی) پیچیدگیِ
مالی/لجستیکیِ زیادی اضافه می‌کند که در MVP لازم نیست. اگر کاربر از فروشگاهِ دیگری هم بخواهد بخرد،
یا باید سبدِ قبلی را ببندد یا (نسخه‌ی بعدی) واقعاً چندسبدی/چندسفارشی کار کند.

این یعنی `CartEntity` یک `vendorId` می‌گیرد و افزودنِ آیتم از ونـدورِ دیگر یا خطا می‌دهد یا سبد را خالی
می‌کند و هشدار می‌دهد (تصمیمِ UX، فازِ ۳).

---

## ۵) فازبندی

### فازِ ۰ — اسکلتِ پروژه
- کپیِ ساختارِ Gradle/Spring Boot از `shop-kotlin-spring-boot` (build.gradle.kts, application.yml, Docker/compose برایِ Postgres)
- کپیِ کاملِ `shop/identity` → `rasteh/identity` (auth/JWT بدون تغییر)
- کپیِ `shop/payment`, `shop/discount` → بدون تغییر
- migration اول: جدولِ `mall`, `vendor`, `vendor_category_mode`

### فازِ ۱ — Vendor + تاییدِ ادمین
- `VendorController` (ثبتِ درخواستِ ونـدور توسطِ کاربر) + `AdminVendorController` (approve/reject/suspend)
- الگو: دقیقاً شبیهِ Admin-approve-therapist در clinic، ولی موجودیتش جدید است
- migration: enum status + ایندکس رویِ `(mallId, status)`

### فازِ ۲ — کاتالوگِ Vendor-scoped
- کپیِ `shop/catalog` کامل → اضافه‌کردنِ `vendorId` به `ProductEntity`/`CategoryEntity`
- هر endpoint ادمینِ کاتالوگ باید چک کند caller همان Vendor است (یا Admin پاساژ) — RBAC جدید
- `VendorCategoryMode` سرویس: تعیینِ Showcase/Commerce per دسته + مشتق‌کردنِ `isPurchasable` رویِ محصول

### فازِ ۳ — سبد/سفارش تک‌ونـدوری
- کپیِ `shop/cart` + اضافه‌کردنِ چکِ vendorId
- کپیِ `shop/order` + `vendorId` رویِ `OrderItem` برایِ گزارش
- `AdminOrderController` per-vendor: هر Vendor فقط سفارش‌هایِ خودش را می‌بیند

### فازِ ۴ — جست‌وجو/کشف چندونـدوری
- کپیِ `ProductSearchRepository` + اضافه‌کردنِ فیلترِ `floorNumber`/`mallId`/`primaryCategory`
- endpoint فهرستِ ونـدورهایِ یک پاساژ با فیلترِ طبقه/دسته
- (اختیاری فازِ بعد) endpoint مسیریابی/نقشه — فقط برگرداندنِ `mapX/mapY` کافی است؛ رندرِ نقشه کارِ کلاینت است

### فازِ ۵ — صیقل
- کپیِ `ReviewService`/`QuestionService` با vendorId
- گزارشِ مالیِ per-vendor در دشبوردِ ادمین پاساژ (شبیهِ فازِ M/N پروژه‌ی فروشگاه)
- Notification: تاییدِ ونـدور، سفارشِ جدید برایِ ونـدور (کپیِ الگویِ `StockNotification`)

---

## ۶) جدولِ «چه‌فایلی از کجا کپی شود» (مرجعِ سریع)

| مقصد در Rasteh | منبع در shop-kotlin-spring-boot | تغییرِ لازم |
|---|---|---|
| `identity/*` | `shop/identity/*` | هیچ |
| `payment/*` | `shop/payment/*` | هیچ |
| `discount/*` | `shop/discount/*` | هیچ |
| `catalog/*` | `shop/catalog/*` | `+vendorId` روی Product/Category، RBAC ادمین |
| `order/*` | `shop/order/*` | `+vendorId` روی OrderItem |
| `cart/*` | `shop/cart/*` | چکِ تک‌ونـدوری‌بودن |
| `vendor/*` (approve flow) | الگو از `clinic/therapist` approve | موجودیت کاملاً جدید |
| `mall/*` | ندارد | جدید |

---

## ۷) قراردادهای ثابت (از پروژه‌ی فروشگاه ادامه بده)

- هر جدولِ جدید → migration idempotent (`CREATE TABLE IF NOT EXISTS`).
- منابعِ محدود (مثلِ رزروِ اسم/اسلاگِ یکتایِ Vendor) → قفلِ pessimistic در صورتِ نیاز.
- ستون‌هایِ JSON (`workingHoursJson`) → `@JdbcTypeCode(SqlTypes.JSON)`؛ برایِ dirty-checking لیست‌ها را reassign کن، نه mutate.
- کدهایِ خطا به سبکِ `VENDOR_NOT_APPROVED`, `PRODUCT_NOT_PURCHASABLE`, `CART_VENDOR_MISMATCH`.

---

## ۸) MVP در برابرِ بعدی

**MVP (فازِ ۰ تا ۴):** ثبتِ ونـدور، تاییدِ ادمین، کاتالوگِ دوحالته، سبد/سفارشِ تک‌ونـدوری، جست‌وجو با فیلترِ طبقه.

**بعدی:** چک‌اوتِ چندونـدوری واقعی، نقشه‌ی تعاملیِ پاساژ (رندرِ گرافیکی، نه فقط مختصات)، پنلِ آنالیتیکسِ Vendor، اشتراکِ پولیِ Vendor برایِ featured-listing، چند-پاساژی (یک اپ برایِ چند مجتمعِ مختلف در شهرهایِ مختلف).
