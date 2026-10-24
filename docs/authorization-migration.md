# Authorization migration and rollback

## Contract

`GET /api/me/capabilities?shopId=` is the client contract. Clients must render and route protected workspaces from capabilities, not `isVendor` or `isAdmin`. A hidden navigation item is never an authorization boundary: every API operation re-evaluates the active grants/membership against its resource.

## Deployment order

1. Apply V6–V8 (expand only), then inspect `authorization_migration_exceptions`.
2. V7 creates `USER`, administrator grants, and owner memberships deterministically. V8 records any unknown legacy role without granting it.
3. Deploy clients that use capability snapshots and the protected-route gate. Legacy role fields remain response-compatible during this window.
4. Remove legacy authorization reads only after the exception table is empty and usage telemetry shows no legacy client.

## Rollback

Application rollback is safe while the compatibility window is active: old columns and tables were not removed. Do not reverse a migration in production. Disable new endpoints/route flags, restore the prior application version, and keep grant/membership/audit records intact. Re-applying V7/V8 is idempotent. To undo an erroneous grant or invitation, revoke it; never delete audit evidence.

## Security decisions

All mutations are default-deny. Ownership transfer is explicitly unsupported: `OWNER` cannot be invited, revoked, or reassigned. Revoke is immediate because the server loads active authorization state for each request. Agent submission requires recorded consent and an active assignment; a maker cannot review their own draft.
