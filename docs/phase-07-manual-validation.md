# Phase 07 manual validation record

The following inputs require an operational/product decision and are intentionally not guessed in code.

| Decision | Safe implementation default | Required owner confirmation |
| --- | --- | --- |
| POS inventory source of truth | `MANUAL` inventory source and explicit ledger adjustments. | Select POS integration, sync direction, reconciliation cadence and failure owner. |
| Per-category freshness SLA | 24-hour confirmation window. Stale BUYABLE offers cannot reserve. | Publish category SLA values and escalation policy. |
| Media storage | Schema stores only private object keys and content metadata. | Select storage/CDN, malware scanning and signed URL lifetime in Task 13. |
| Product duplicate merge | Pending admin queue only; no automatic merge. | Define reviewer workflow and confidence threshold. |
| Analytics sink | Durable `catalog_events` records update, freshness, inventory and reservation transitions. | Select analytics/outbox transport and retention policy. |

## Manual acceptance scenarios

- **Scheduler:** run two application instances against the same PostgreSQL database; only one must acquire the advisory lock for an expiry cycle.
- **Media:** connect the Task 13 private-storage adapter, then verify a public/bucket URL cannot be attached and a signed private object can.
- **CSV:** import a file with one valid and one invalid row; the valid row must persist while the invalid row reports its source row and error.
