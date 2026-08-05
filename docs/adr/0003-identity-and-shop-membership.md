# ADR 0003: هویت، عضویت فروشگاه و maker-checker

- Status: Accepted
- Date: 2026-08-05
- Decision owner: مالک محصول و مالک عملیات

## Context

global role `VENDOR` مالکیت چند فروشگاه و تفکیک اختیار را مدل نمی‌کند.

## Decision

Platform roleها: `USER`، `FIELD_AGENT`، `AGENT_SUPERVISOR`، `ADMIN` و `SUPERADMIN` هستند. عضویت فروشگاه مستقل است: `OWNER`، `MANAGER`، `CATALOG_EDITOR`، `ORDER_OPERATOR` و `SUPPORT`. اختیار هر endpoint با permission صریح و scope مالکیت/عضویت server-side بررسی می‌شود. agent فقط draft و evidence می‌سازد و نمی‌تواند کار خود را approve کند.

## Consequences

- UI guard فقط UX است؛ سرور مرجع authorization است.
- تغییر role، مالکیت، approval و اطلاعات تسویه audit immutable می‌خواهد.
- `/api/admin/**` deny-by-default است.

## Alternatives

vendor به‌عنوان global role رد شد، چون multi-role و multi-shop را ایمن مدل نمی‌کند.

## Migration

roleهای legacy به grant/membership نگاشت می‌شوند؛ تا پایان compatibility window هم‌زمان read می‌شوند و write جدید به مدل canonical می‌رود.

## Revisit trigger

افزودن نقش جدید، delegation مالی یا تغییر policy approval.
