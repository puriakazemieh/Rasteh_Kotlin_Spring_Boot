# ADR 0004: مهاجرت commerce به مدل canonical

- Status: Accepted
- Date: 2026-08-05
- Decision owner: مالک فنی سرور؛ تأیید انتشار توسط مالک محصول

## Context

stack کلاسیک catalog/variant/inventory و stack marketplace/shop_products/orders هم‌پوشانی و ناسازگاری دارند.

## Decision

مدل canonical این است: `MerchantOrganization`، `ShopLocation`، `Product`، `Variant`، `ShopOffer`، `InventoryPosition` و در آینده `Order`. مدل کلاسیک catalog/variant/inventory پایهٔ غنی‌تر canonical است؛ رابطهٔ shop و داده‌های marketplace به آن منتقل می‌شود. API canonical زیر `/api/v1` منتشر می‌شود.

## Consequences

- write جدید به stack legacy افزوده نمی‌شود؛ featureهای legacy freeze می‌شوند.
- compatibility window پیشنهادی: 90 روز پس از عرضهٔ پایدار `/api/v1` و reconciliation با اختلاف صفر.
- حذف فقط در release جداگانه و پس از telemetry، contract test و owner sign-off انجام می‌شود.

## Alternatives

ادامهٔ دو stack رد شد؛ حذف فوری legacy نیز به‌دلیل ریسک data loss و client breakage رد شد.

## Migration

expand schema → backfill قابل rehearsal → dual-read محدود با telemetry → انتقال write → reconciliation → freeze → contract. migrationها forward-only؛ rollback از طریق roll-forward یا restore برنامه‌ریزی‌شده است، نه DDL مخرب.

## Revisit trigger

انقضای compatibility window، اختلاف reconciliation، یا نیاز contract consumer معتبر.
