# ADR 0002: پول و قیمت‌گذاری دقیق

- Status: Accepted
- Date: 2026-08-05
- Decision owner: مالک محصول؛ PSP/finance owner برای هر جریان پرداخت

## Context

مدل‌های فعلی از `Double` استفاده می‌کنند و مقدار gateway/wallet و callback قابل‌اتکا نیست.

## Decision

قرارداد canonical پول `Money(minorUnits: Long, currency: "IRR")` است. تمام API، persistence، snapshot و محاسبه فقط integer minor unit یا value object دقیق می‌پذیرند. تومان فقط presentation است و هرگز قرارداد ذخیره‌سازی/API نیست. در MVP رزروی، قیمت اطلاع‌رسانی است و با `lastConfirmedAt` و `expiresAt` عرضه می‌شود؛ هیچ پولی توسط راسته دریافت یا تسویه نمی‌شود.

## Consequences

- `Double` و `Float` برای پول ممنوع‌اند.
- price snapshot، discount، tax/fee و amount در آینده همگی `Money` هستند.
- هیچ PSP، split settlement، wallet، refund یا payment migration تا تعیین provider و interpretation حقوقی طراحی نمی‌شود.

## Alternatives

- ذخیرهٔ تومان: با قرارداد پیشنهادی و mapping PSP سازگار نیست؛ رد شد.
- decimal بدون currency: ambiguity و خطر گردکردن دارد؛ رد شد.

## Migration

مهاجرت مالی فقط expand/backfill/dual-read-write و با contract test، reconciliation و rollback/roll-forward جداگانه انجام می‌شود.

## Revisit trigger

انتخاب PSP مجاز، قرارداد settlement و تأیید حقوقی/مالی برای payment واقعی.
