# پلنِ ساخت — Rasteh_Kotlin_Spring_Boot (سرور)

> مارکت‌پلیسِ محلیِ پاساژ/راسته (ترکیبی از دیوار/شیپور/باسلام/ترب): چند فروشگاهِ مستقل زیرِ یک چتر،
> با سه نقشِ یکپارچه (خریدار / فروشنده / ادمینِ پاساژ)، کاتالوگِ دوحالته
> («خرید آنلاین» = buyable / «فقط بازدید حضوری» = visitOnly)، سیستمِ دنبال‌کردن + هدیهٔ دنبال‌کننده،
> چتِ درون‌برنامه‌ای، پیشنهادِ قیمت، و ۲۰ قابلیتِ ویژه (لایوشاپینگ، خرید گروهی، تخفیف ساعتی،
> مسیریابِ داخلِ پاساژ، وفاداری، پرداختِ امانی و…).

این سند خروجیِ **تلفیقِ دو منبع** است:
1. تحلیلِ اورلپ با پروژه‌ی خواهرِ تک‌مستأجری (`shop-kotlin-spring-boot`) — ~۴۰-۵۰٪ زیرساخت قابلِ کپی.
2. **بستهٔ طراحیِ نهایی** (`design_handoff_unified_app` — «اپلیکیشن بازارچهٔ محلی»، hi-fi، RTL فارسی)
   که ساختارِ صفحات، مدلِ داده، و ۲۰ قابلیتِ ویژه را قطعی می‌کند.

نسخهٔ هم‌سویِ کلاینت: `RASTEH_KMP_PLAN.md` در ریپویِ `Rasteh_KMP`.

---

## ۰) منبعِ حقیقت: بستهٔ طراحی

مرجعِ نهاییِ UI/UX و دامنه، پوشهٔ `design_handoff_unified_app` است:
- `Unified App.dc.html` — اپِ کامل (~۵۸ صفحه، همه در یک state machine با متغیرِ `screen`).
- `Local-Marketplace-Spec.dc.html` — سندِ فلوی اولیه.
- `README.md` — توکن‌های طراحی، مدلِ داده، فهرستِ صفحات و رفتار.

**قاعده:** هر تصمیمِ محصولی که این پلن پوشش نمی‌دهد، از روی این فایل‌ها استخراج شود، نه از حدس.
سرور باید همهٔ داده‌ای را که این صفحات نمایش می‌دهند، فراهم کند (contract-first از روی صفحات).

---

## ۱) تصمیمِ تکنولوژی

**همان استکِ اثبات‌شدهٔ پروژه‌ی فروشگاه** ادامه می‌یابد (بازاستفادهٔ حداکثری + آشناییِ تیم):

- Kotlin + Spring Boot (Web MVC یا WebFlux — هرچه در `shop-kotlin-spring-boot` هست، همان).
- PostgreSQL + Hibernate/JPA؛ migration با فایل‌هایِ idempotent در `db/init/NNN_*.sql`.
- Clean Architecture لایه‌ای: `api` (controller/dto/mapper) → `application` (service) → `persistence` (entity/repository).
- JWT auth (کپی از `shop/identity`).
- پرداخت: Zarinpal/IDPay از طریقِ همان `shop/payment` (کپی مستقیم) — با افزودنِ حالتِ **امانی/escrow**.
- ذخیرهٔ فایل/تصویر: همان الگویِ `shop` (آپلود یا «افزودن با لینک»).

> نکتهٔ طراحی: تصاویر در پروتوتایپ با **ایموجی + placeholder** جایگزین شده‌اند؛ سرور فیلدِ `emoji`
> و `imageUrl[]` را هردو نگه می‌دارد تا کلاینت در نبودِ تصویرِ واقعی، fallbackِ ایموجی داشته باشد.

---

## ۲) نقش‌ها (سه نقشِ یکپارچه در یک اپ)

طراحی یک اپِ واحد با **سوییچِ نقشِ درون‌پروفایلی** دارد (`role = customer | vendor | admin`).
سرور باید RBAC سه‌سطحی بدهد و یک کاربر بتواند هم‌زمان customer و vendor (و در صورتِ اعطا، admin) باشد.

| نقش | معادلِ فروشگاه | تفاوت |
|---|---|---|
| Customer | `customer` | + جست‌وجوی چندونـدوری، دنبال‌کردن، بوکمارک، چت، پیشنهادِ قیمت، ۲۰ قابلیت |
| Vendor (صاحبِ فروشگاه) | نزدیک‌ترین: `clinic/TherapistEntity` (ارائه‌دهنده‌ای که ادمین تأییدش می‌کند) | + صاحبِ کاتالوگِ خودش، آمار، تخفیفِ دنبال‌کننده، QR |
| Admin پاساژ | `admin` | + صفِ تأییدِ فروشگاه، مدیریتِ گزارشِ تخلف، سوپروایزرها |

`RoleEntity` مقدارِ `VENDOR` می‌گیرد. یک کاربر می‌تواند چند نقش داشته باشد؛ نقشِ فعال سمتِ کلاینت است.

---

## ۳) مدلِ داده (منطبق بر بخشِ Data Models طراحی)

فیلدهای زیر مستقیماً از objectهای دادهٔ `Unified App.dc.html` استخراج شده‌اند.

### هستهٔ مارکت‌پلیس — جدید

```
Mall / Pasaj      (id, name, city, address, floorCount, description, floorLabelsJson)
City              (id, name, province, isActive)           -- صفحهٔ citySelect
Shop (=Vendor)    (id, mallId, ownerUserId, name, category(=primaryCategory), floor,
                   type: BUYABLE|VISIT_ONLY,                 -- «خرید آنلاین» / «فقط بازدید حضوری»
                   verified: Boolean, rating, reviewsCount,
                   followersCount, salesCount, satisfaction, performance,  -- نوارِ آمارِ shopDetail
                   phone, hasChat: Boolean, acceptsOffers: Boolean,        -- toggleهای becomeVendor
                   about, workingHoursJson, address, mapX, mapY,           -- موقعیت روی نقشهٔ داخلی
                   emoji, coverUrl, logoUrl,
                   status: PENDING|APPROVED|REJECTED|SUSPENDED,
                   createdAt, approvedAt)
VendorCategoryMode (shopId, categoryId, mode: SHOWCASE|COMMERCE)  -- override per دسته روی type فروشگاه
```

### کاتالوگ — اقتباس از `shop/catalog` + `shopId`

```
ProductEntity     += shopId (FK), condition: NEW|USED, stock, discountPercent, oldPrice,
                     isPurchasable (مشتق از type فروشگاه + VendorCategoryMode), emoji
CategoryEntity    += shopId (nullable برای دسته‌بندیِ سراسری)
ProductImageEntity, OptionType/OptionValue (=مدل/model chip), InventoryEntity  -- ساختار بدون تغییر، از طریقِ product.shopId اسکوپ‌شونده
```

### دنبال‌کردن + هدیهٔ دنبال‌کننده — جدید (قلبِ تجربهٔ طراحی)

```
ShopFollow        (userId, shopId, createdAt)                 -- toggle در shopDetail/manageFollowing
FollowerPerk      (id, shopId, code, percent, label, expiry, usesCount, showSpecialPrice: Boolean, active)
                                                              -- FOLLOW15 و ساختِ کدِ جدید توسطِ ونـدور
```

### تعامل — جدید

```
Conversation      (id, customerUserId, shopId, lastMessageAt)  -- chatList
Message           (id, conversationId, senderUserId, body, sentAt, readAt)  -- chatThread
Offer             (id, productId, buyerUserId, shopId, amount, status: PENDING|ACCEPTED|REJECTED, createdAt)
                                                              -- makeOffer («پذیرش پیشنهاد قیمت»)
Bookmark          (userId, productId?, shopId?, createdAt)     -- bookmarks (تب محصولات/فروشگاه‌ها)
ActivityEvent     (id, userId, action: LIKE|SAVE|MESSAGE|FOLLOW, targetType, targetId, icon, createdAt)  -- activity
Review            (id, productId?, shopId?, userId, stars, comment, createdAt)  -- {name, stars, comment}
Report            (id, reporterUserId, targetType: SHOP|PRODUCT, targetId, reason, note, photoUrls,
                   status: OPEN|RESOLVED|REJECTED, createdAt)  -- «گزارش تخلف» + adminDash/reportDetail
Notification      (id, userId, type, title, body, isRead, createdAt)  -- زنگولهٔ هدر + صفحهٔ notifications
```

### سبد/سفارش — اقتباس از `shop`

```
CartEntity        += shopId (سبدِ تک‌ونـدوری — بخشِ ۵)
CartItemEntity
OrderEntity       (تک‌ونـدوری)، OrderItemEntity += shopId (گزارش‌گیریِ per-vendor)
```

### مالی/وفاداری — جدید (از صفحاتِ wallet/loyalty/giftcard/vipsub/referral)

```
Wallet            (userId, balance)                           -- wallet
WalletTransaction (id, walletId, amount, type, ref, createdAt)
LoyaltyAccount    (userId, points, tier: SILVER|GOLD|PLATINUM) -- loyalty (کارت طلایی + QR)
LoyaltyReward     (id, title, cost, active)
GiftCard          (id, code, amount, buyerUserId, recipient, redeemedByUserId?, status)  -- giftcard
Referral          (id, inviterUserId, code, inviteeUserId?, rewardStatus)  -- referral («دعوت دوستان»)
Subscription      (userId, plan: BAZARCHE_PLUS, startedAt, expiresAt, active)  -- vipsub («بازارچه پلاس»)
```

### بدون تغییر — کپیِ مستقیم

```
UserEntity, RoleEntity (+VENDOR), DiscountEntity, CampaignEntity, PaymentTransactionEntity
```

---

## ۴) مدلِ دادهٔ ۲۰ قابلیتِ ویژه (features)

هر قابلیت یک صفحه در `features` دارد. جدولِ زیر تعیین می‌کند هرکدام چه پشتیبانیِ سروری می‌خواهد و در کدام فاز است.

| # | قابلیت (screen) | نیازِ سرور | فاز |
|---|---|---|---|
| 1 | concierge (دستیارِ خریدِ هوشمند) | endpoint چت‌بات؛ در MVP پاسخِ قالبی/جست‌وجو، بعداً LLM | F7 |
| 2 | wayfind / floorMap (مسیریابِ داخلِ پاساژ) | برگرداندنِ `mapX/mapY/floor` فروشگاه‌ها + متادیتای طبقات | F6 |
| 3 | live (لایوشاپینگ) | `LiveSession` (shopId, streamUrl, status)، چتِ زنده، کارتِ محصولِ pinned | F7 |
| 4 | flash (تخفیفِ ساعتی) | `FlashSale` (productId, percent, startsAt, endsAt, stock) — توسعهٔ discount | F5 |
| 5 | groupbuy (خرید گروهی) | `GroupBuy` (productId, targetQty, currentQty, price, deadline) + `GroupBuyParticipant` | F5 |
| 6 | loyalty (باشگاهِ وفاداری) | LoyaltyAccount/Reward (بخشِ ۳) | F5 |
| 7 | appointment (رزروِ نوبتِ حضوری) | `Appointment` (shopId, userId, slotStart, status) + ظرفیتِ اسلات | F6 |
| 8 | compare (مقایسهٔ فروشندگان) | کوئریِ «فروشندگانِ دیگرِ این کالا» + ارزان‌ترین (بخشِ listing) | F4 |
| 9 | escrow (پرداختِ امانِ امانی) | حالتِ نگه‌داریِ وجه در PaymentTransaction + تایم‌لاینِ مرحله‌ای | F7 |
| 10 | tracking (ردیابیِ زندهٔ سفارش) | `OrderTracking` (orderId, stage, courier, lat/lng) | F7 |
| 11 | returns (مرکزِ بازگشتِ کالا) | `ReturnRequest` (orderItemId, reason, photoUrls, status) | F6 |
| 12 | giftcard (کارتِ هدیه) | GiftCard (بخشِ ۳) | F6 |
| 13 | warranty (دفترچهٔ ضمانتِ دیجیتال) | `Warranty` (orderItemId, serial, validUntil) | F6 |
| 14 | events (رویدادها/جشنواره‌ها) | `Event` (mallId, title, date, description) | F6 |
| 15 | community (انجمنِ محله) | `CommunityPost` + `Comment` (اجتماعی) | F7 |
| 16 | pricealert (هشدارِ کاهشِ قیمت) | `PriceAlert` (userId, productId, targetPrice) + تریگرِ نوتیف | F5 |
| 17 | stories (استوریِ فروشگاه‌ها) | `Story` (shopId, mediaUrl, expiresAt) | F6 |
| 18 | vipsub (بازارچه پلاس) | Subscription (بخشِ ۳) + پرداختِ دوره‌ای | F7 |
| 19 | parking (پارکینگِ من) | `ParkingSession` (userId, spot, enteredAt, fee, paid) | F7 |
| 20 | visualsearch (جست‌وجوی تصویری/صوتی) | آپلودِ تصویر + تطبیق (ML) — MVP: صرفاً آپلود+جست‌وجوی متنی | F7+ |

**MVP (F0–F4)** فقط قابلیت ۸ (compare) را واجب دارد؛ بقیه از F5 به بعد موج‌به‌موج اضافه می‌شوند.
launcherِ `features` می‌تواند از ابتدا موجود باشد و قابلیت‌های نیامده را «به‌زودی» نشان دهد.

---

## ۵) تصمیمِ کلیدیِ معماری: سبدِ خریدِ تک‌ونـدوری

**MVP: سبدِ خرید تک‌ونـدوری** (مثلِ pickupِ محلی). چک‌اوتِ چندونـدوری (splitِ پرداخت بینِ چند فروشگاه،
ارسال‌های جدا، لغوِ جزئی) پیچیدگیِ مالی/لجستیکیِ بالایی دارد و برای MVP لازم نیست.

`CartEntity` یک `shopId` می‌گیرد؛ افزودنِ آیتم از فروشگاهِ دیگر → خطای `CART_VENDOR_MISMATCH`
(کلاینت هشدارِ «سبد را خالی می‌کنید؟» می‌دهد — تصمیمِ UX در فازِ F3 کلاینت).
برای فروشگاه‌های `VISIT_ONLY` دکمهٔ خرید نمایش داده نمی‌شود؛ به‌جایش «تماس/مسیریابی/رزروِ بازدید».

---

## ۶) فازبندی

### فازِ ۰ — اسکلت + دیزاین‌کانترکت
- کپیِ ساختارِ Gradle/Spring Boot از `shop-kotlin-spring-boot` (build.gradle.kts، application.yml، docker-compose برای Postgres).
- کپیِ کاملِ `shop/identity` → `rasteh/identity` (auth/JWT بدون تغییر) + افزودنِ نقشِ `VENDOR` و چند-نقشی.
- کپیِ `shop/payment`, `shop/discount` بدون تغییر.
- migration اول: `city`, `mall`, `shop`, `vendor_category_mode`.
- سندِ contract: فهرستِ ~۵۸ صفحه از طراحی → جدولِ endpointهای موردِ نیاز.

### فازِ ۱ — Vendor onboarding + تأییدِ ادمین
- `ShopController` (ثبتِ درخواستِ فروشگاه: نام، دسته، طبقه، `type`، تلفن، آدرس، ساعاتِ کاری، about،
  toggleهای `hasChat`/`acceptsOffers`) — منطبق بر صفحهٔ **becomeVendor**.
- `AdminShopController` (approve/reject/suspend) — الگویِ Admin-approve-therapist در clinic.
- `CityController` (فهرست/جست‌وجوی شهر) — صفحهٔ **citySelect**.
- migration: enum status + ایندکسِ `(mallId, status)`, `(city)`.

### فازِ ۲ — کاتالوگِ دوحالته (Vendor-scoped)
- کپیِ `shop/catalog` + افزودنِ `shopId` به Product/Category + فیلدهای `condition/stock/discountPercent/oldPrice/emoji`.
- RBAC: هر endpointِ ادمینِ کاتالوگ چک کند caller همان Vendor است (یا Admin پاساژ).
- `VendorCategoryMode` سرویس: Showcase/Commerce per دسته + مشتق‌کردنِ `isPurchasable`.
- منطبق بر صفحاتِ **addProduct / manageListings / editShop**.

### فازِ ۳ — دنبال‌کردن + هدیهٔ دنبال‌کننده + چت + پیشنهادِ قیمت
- `FollowController` (toggle follow، فهرستِ دنبال‌شده‌ها) — **shopDetail / manageFollowing**.
- `FollowerPerkController` (ونـدور کدِ FOLLOW15 می‌سازد؛ خریدارِ دنبال‌کننده استفاده می‌کند) — **followerPerk**.
- `ChatController` + WebSocket/polling برای Conversation/Message — **chatList / chatThread**.
- `OfferController` (ثبت/پذیرش/ردِ پیشنهادِ قیمت، فقط اگر `shop.acceptsOffers`) — **makeOffer**.
- `BookmarkController`, `ActivityController` — **bookmarks / activity**.

### فازِ ۴ — سبد/سفارش + جست‌وجو/مقایسه/کشف
- کپیِ `shop/cart` + چکِ تک‌ونـدوری؛ کپیِ `shop/order` + `shopId` روی OrderItem — **cart / orderConfirm / vendorOrders**.
- کپیِ `ProductSearchRepository` + فیلترهایِ طراحی: **دسته، قیمت، برند، وضعیت(new/used)، مرتب‌سازی، طبقه، شهر** — **search**.
- کوئریِ «فروشندگانِ دیگرِ این کالا» + ارزان‌ترین (بوردر سبز/badge) — **listing / compare**.
- `ReviewController` (نظرات + میانگین) — تبِ نظراتِ **shopDetail**.
- endpointِ «کالاها و سفارش‌های من» (کالاها/سفارش‌هایِ خودِ کاربر، اسکوپ به ownerUserId) — **myListings** (بازاستفاده از order/catalog). خروجِ کاربر (**logoutConfirm**) صرفاً کلاینتی است (پاک‌کردنِ توکن).

### فازِ ۵ — قابلیت‌هایِ تجاریِ موجِ اول
- `FlashSaleController` (تخفیفِ ساعتی + شمارشِ معکوس + موجودی) — **flash**.
- `GroupBuyController` (ظرفیت + شرکت‌کننده‌ها) — **groupbuy**.
- `LoyaltyController` (امتیاز/سطح/جوایز/QR) — **loyalty**.
- `PriceAlertController` (+ تریگرِ نوتیف) — **pricealert**.

### فازِ ۶ — قابلیت‌هایِ خدماتی + نقشه
- `FloorMapController` (`mapX/mapY/floor` + متادیتای طبقات) — **wayfind / floorMap**.
- `AppointmentController` (رزروِ بازدید) — **appointment**.
- `ReturnController`, `WarrantyController`, `GiftCardController`, `EventController`, `StoryController`.

### فازِ ۷ — قابلیت‌هایِ پیشرفته
- `LiveSessionController` (لایوشاپینگ + چتِ زنده + محصولِ pinned) — **live**.
- Escrow (نگه‌داریِ وجهِ امانی + تایم‌لاین) — **escrow**؛ `OrderTrackingController` — **tracking**.
- `SubscriptionController` (بازارچه پلاس) — **vipsub**؛ `ParkingController` — **parking**.
- `CommunityController` (پست/کامنت) — **community**؛ `ConciergeController` (چت‌بات) — **concierge**؛ visualsearch.

### فازِ ۸ — صیقل
- گزارشِ مالیِ per-vendor در دشبوردِ ادمین (شبیهِ فازِ M/N فروشگاه) — **vendorAnalytics**.
- Notificationها: تأییدِ ونـدور، سفارشِ جدید، پیشنهادِ قیمت، هشدارِ قیمت، پاسخِ چت (کپیِ الگویِ `StockNotification`).
- ماژراسیونِ گزارشِ تخلف: `AdminReportController` — **adminDash / reportDetail**؛ سوپروایزرها.

---

## ۷) جدولِ «چه‌فایلی از کجا کپی شود» (مرجعِ سریع)

| مقصد در Rasteh | منبع در shop-kotlin-spring-boot | تغییرِ لازم |
|---|---|---|
| `identity/*` | `shop/identity/*` | `+VENDOR`، چند-نقشی بودنِ کاربر |
| `payment/*` | `shop/payment/*` | + حالتِ escrow (فازِ ۷) |
| `discount/*` | `shop/discount/*` | مبنایِ flash/groupbuy/perk |
| `catalog/*` | `shop/catalog/*` | `+shopId`، condition/stock/discount، RBAC ادمینِ ونـدور |
| `order/*` | `shop/order/*` | `+shopId` روی OrderItem، tracking/returns/warranty |
| `cart/*` | `shop/cart/*` | چکِ تک‌ونـدوری |
| `review/*`, `question/*` | `shop/review/*` | اسکوپ به shopId/productId |
| `shop/*` (approve flow) | الگو از `clinic/therapist` approve | موجودیتِ Shop کاملاً جدید |
| `mall/*`, `city/*`, `follow/*`, `chat/*`, `offer/*`, `wallet/*`, `loyalty/*`, feature-ها | ندارد | جدید (از روی طراحی) |

---

## ۸) قراردادهای ثابت (از پروژه‌ی فروشگاه ادامه بده)

- هر جدولِ جدید → migration idempotent (`CREATE TABLE IF NOT EXISTS`) در `db/init/NNN_*.sql`.
- منابعِ محدود (رزروِ نامِ/اسلاگِ یکتایِ Shop، ظرفیتِ groupbuy، اسلاتِ appointment، موجودیِ flash)
  → قفلِ pessimistic در صورتِ نیاز تا از over-sell/over-book جلوگیری شود.
- ستون‌هایِ JSON (`workingHoursJson`, `floorLabelsJson`) → `@JdbcTypeCode(SqlTypes.JSON)`؛
  برای dirty-checking لیست‌ها را reassign کن، نه mutate.
- کدهایِ خطا به سبکِ فروشگاه: `VENDOR_NOT_APPROVED`, `PRODUCT_NOT_PURCHASABLE`, `CART_VENDOR_MISMATCH`,
  `SHOP_DOES_NOT_ACCEPT_OFFERS`, `PERK_ALREADY_USED`, `FLASH_SOLD_OUT`, `GROUPBUY_CLOSED`.
- **RTL/فارسی سمتِ کلاینت است**، ولی سرور اعداد را **خام** (لاتین/عدد) برگرداند؛ تبدیل به ارقامِ فارسی وظیفهٔ کلاینت است.
  رشته‌های نمایشیِ فارسی (نامِ دسته، وضعیت) از سرور می‌آیند.
- توکن‌های طراحی (رنگ/فونت/شعاع) صرفاً کلاینتی‌اند؛ سرور دخالتی ندارد جز فراهم‌کردنِ `emoji`/`color` placeholder برای هر موجودیت.

---

## ۹) MVP در برابرِ بعدی

**MVP (فازِ ۰ تا ۴):** ثبتِ ونـدور با تأییدِ ادمین، انتخابِ شهر، کاتالوگِ دوحالته (buyable/visitOnly)،
دنبال‌کردن + هدیهٔ دنبال‌کننده، چت، پیشنهادِ قیمت، بوکمارک، سبد/سفارشِ تک‌ونـدوری،
جست‌وجو با فیلترِ طبقه/شهر/وضعیت + مقایسهٔ فروشندگان، نظرات.

**موجِ بعدی (فازِ ۵ به بعد):** ۲۰ قابلیتِ ویژه (flash/groupbuy/loyalty/pricealert →
wayfind/appointment/returns/warranty/giftcard/events/stories →
live/escrow/tracking/vipsub/parking/community/concierge/visualsearch).

**آینده:** چک‌اوتِ چندونـدوریِ واقعی، اپِ ونـدورِ جدا، آنالیتیکسِ ونـدور، featured-listingِ پولی،
چند-پاساژی/چندشهری (یک اپ برای چند مجتمع در شهرهای مختلف).
