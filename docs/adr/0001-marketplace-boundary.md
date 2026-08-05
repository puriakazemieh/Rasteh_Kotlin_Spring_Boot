# ADR 0001: مرز MVP بازارگاه

- Status: Accepted
- Date: 2026-08-05
- Decision owner: مالک محصول؛ بازبینی حقوقی پیش از هر پرداخت واقعی

## Context

دو stack تجارت موازی، نقش حقوقی نامشخص و مسیرهای پرداخت ناامن، انتشار marketplace تراکنشی را در پایلوت پرریسک می‌کند.

## Decision

MVP «فهرست محلی + ویترین + رزرو/استعلام» است. راسته معرفی‌کننده و تسهیل‌گر ارتباط/رزرو است، نه فروشنده، نگه‌دارندهٔ وجه یا تسویه‌کننده. `SHOWCASE` و `RESERVABLE` فعال‌اند؛ `BUYABLE` تا تکمیل تصمیم شریک پرداخت، تسویه، بازپرداخت و کنترل‌های P0 غیرفعال پیش‌فرض می‌ماند. خرید و بازپرداخت در MVP در رابطهٔ مستقیم مشتری و فروشگاه است؛ راسته فقط رسیدگی به گزارش و پشتیبانی محصول را طبق policy منتشرشده انجام می‌دهد.

## Consequences

- هر قابلیت wallet، gift card، escrow، settlement، payout، commission، subscription پولی، flash sale و checkout باید disabled باشد و به‌عنوان completed عرضه نشود.
- قیمت و موجودی فقط با freshness نشان داده می‌شوند؛ رزرو، quote و لغو آن باید lifecycle و audit داشته باشند.
- پیش از فعال‌سازی `BUYABLE`، مشاور حقوقی و شریک پرداخت مجاز باید مسئولیت refund/dispute، نگهداری وجه و حریم خصوصی را تأیید کنند.

## Alternatives

- directory + showcase: ریسک کمتر، اما امکان سنجش intent خرید/رزرو محدودتر.
- transactional marketplace single-shop: ارزش بالاتر، اما اکنون به PSP، settlement و کنترل‌های مالی آماده نیاز دارد؛ رد شد.

## Migration

APIهای مالی قدیمی read-only/frozen می‌شوند؛ UI و routeهای آن‌ها در releaseهای بعد disabled-by-default خواهند بود. هیچ حذف یا migration مخرب در این ADR مجاز نیست.

## Revisit trigger

پس از موفقیت pilot رزروی و وجود قرارداد شریک پرداخت، policy بازپرداخت و تکمیل taskهای 01، 02، 03 و 12.
