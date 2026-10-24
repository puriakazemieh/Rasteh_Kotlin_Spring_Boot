# Phase 07 manual validation record

The following inputs require an operational/product decision and are intentionally not guessed in code.

| Decision | Safe implementation default | Required owner confirmation |
| --- | --- | --- |
| POS inventory source of truth | `MANUAL` inventory source and explicit ledger adjustments. | Select POS integration, sync direction, reconciliation cadence and failure owner. |
| Per-category freshness SLA | 24-hour confirmation window. Stale BUYABLE offers cannot reserve. | Publish category SLA values and escalation policy. |
| Media storage | Schema stores only private object keys and content metadata. | Select storage/CDN, malware scanning and signed URL lifetime in Task 13. |
| Product duplicate merge | Pending admin queue only; no automatic merge. | Define reviewer workflow and confidence threshold. |
| Analytics sink | Domain events remain API-level observability points. | Select analytics/outbox transport and retention policy. |
