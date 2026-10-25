# Phase 06 manual validation record

Product approval was supplied on 2026-10-24 for the generic onboarding policy below.

| Item | Implemented default | Manual production check |
| --- | --- | --- |
| Evidence categories | Generic private evidence only; no category may require a document until a specialist rule is added. | Confirm category rules with legal/compliance before enforcing them. |
| Object storage | `private/` object-key prefix, SHA-256 metadata and optional retention deadline. The API never returns a public URL. | Configure private bucket IAM, signed-download endpoint and retention lifecycle. |
| Geospatial search | Validated lat/lng plus indexed Haversine query. | Run `EXPLAIN ANALYZE` with production coordinates; migrate implementation to PostGIS only after the managed database extension is enabled. |
| Indoor graph | Cluster → Building → Floor → Corridor → Unit schema. | Import a surveyed floor plan before enabling graphical routing; routing is intentionally out of scope. |
| Duplicate resolution | Detect and place in `NEEDS_CHANGES`; reviewer supplies `duplicateOfId`. | Reviewer must decide merge/correction; no automatic deletion or merge occurs. |
| Claim/OTP | Authenticated claimant verifies the existing OTP and a separate reviewer approves. | Verify SMS delivery, OTP rate limits and support recovery in staging. |
| Legacy registration migration | `POST /api/shops` now responds with `410 Gone`; only `/api/shop-applications` may create onboarding state. | Verify released clients use the wizard endpoints before rolling this API change to production. |
| Agent-assisted onboarding | Field agent creates a draft through `/api/agent/shop-applications`; the applicant remains the owner/claimant. | Verify the assignment policy and field workflow with an actual agent account. |
