# ADR 0005: محدودهٔ پایلوت، providerها و انتشار

- Status: Accepted
- Date: 2026-08-05
- Decision owner: مالک محصول؛ مالک عملیات برای provider و release owner برای هر target

## Context

گسترهٔ چهار platform و providerهای نامشخص، proof of value و عملیات پایلوت را پراکنده می‌کند.

## Decision

پایلوت در تهران، محدودهٔ تجاری لاله‌زار اجرا می‌شود. category نخست الکتریکی است و category دوم در این چرخه تعریف نشده است؛ افزودن آن نیازمند ADR/revisit است. Android و Web تنها launch channelهای MVP هستند. iOS و Desktop experimental بوده و بدون owner، signing واقعی، crash reporting و rollback evidence منتشر نمی‌شوند.

providerهای map، SMS، push، storage و analytics پشت adapter قراردادی‌اند. برای MVP fallbackها به‌ترتیب آدرس/مسیریابی بدون نقشه، ثبت درخواست بدون ادعای ارسال SMS، اعلان درون‌برنامه‌ای و رد امن upload هستند. انتخاب provider اصلی/fallback واقعی، SLA، sandbox، credential rotation و owner عملیاتی به Task 14 موکول شده و پیش از production در runbook ثبت می‌شود. storage production object storage + CDN است؛ analytics حداقلی و بدون PII است. payment تا قرارداد provider مجاز disabled است.

## Consequences

- Web نیازمند HTML معنایی، SSR/prerender، canonical URL، sitemap و validation indexability است.
- Android/Web بدون signing، versioning، crash reporting و rollback evidence release نمی‌شوند.
- iOS/Desktop در status matrix Experimental باقی می‌مانند.

## Alternatives

launch هم‌زمان چهار platform یا انتخاب provider در کد رد شد؛ هزینهٔ عملیات و lock-in را بالا می‌برد.

## Migration

adapterها قرارداد stable دارند؛ تعویض provider با dual delivery/dry run مناسب provider و بدون افشای secret انجام می‌شود.

## Revisit trigger

نام‌گذاری cluster/category، افزایش scope pilot، یا درخواست انتشار platform جدید.
