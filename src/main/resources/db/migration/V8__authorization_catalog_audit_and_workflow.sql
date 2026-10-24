-- Expand-only authorization hardening. No legacy role or owner column is removed.
ALTER TABLE platform_role_grants ADD COLUMN IF NOT EXISTS revoked_by_user_id BIGINT REFERENCES users(id);
ALTER TABLE shop_memberships ADD COLUMN IF NOT EXISTS accepted_at TIMESTAMPTZ;
ALTER TABLE shop_memberships ADD COLUMN IF NOT EXISTS revoked_by_user_id BIGINT REFERENCES users(id);
ALTER TABLE agent_assignments ADD COLUMN IF NOT EXISTS revoked_at TIMESTAMPTZ;
ALTER TABLE agent_assignments ADD COLUMN IF NOT EXISTS created_by_user_id BIGINT REFERENCES users(id);
ALTER TABLE agent_drafts ADD COLUMN IF NOT EXISTS review_decision VARCHAR(16);
ALTER TABLE agent_drafts ADD COLUMN IF NOT EXISTS review_note TEXT;
ALTER TABLE agent_drafts ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
CREATE INDEX IF NOT EXISTS idx_role_grants_active ON platform_role_grants(user_id, active);
CREATE INDEX IF NOT EXISTS idx_membership_invites ON shop_memberships(user_id, status);
CREATE TABLE IF NOT EXISTS authorization_migration_exceptions (id UUID PRIMARY KEY, legacy_table VARCHAR(64) NOT NULL, legacy_id VARCHAR(128) NOT NULL, reason VARCHAR(255) NOT NULL, details JSONB NOT NULL DEFAULT '{}'::jsonb, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), UNIQUE(legacy_table, legacy_id, reason));
INSERT INTO authorization_migration_exceptions(id, legacy_table, legacy_id, reason, details)
SELECT md5('role-null:' || id)::uuid, 'users', id::text, 'UNMAPPED_LEGACY_ROLE', jsonb_build_object('role', role::text)
FROM users WHERE role IS NULL OR role::text NOT IN ('CUSTOMER','VENDOR','ADMIN','SUPERADMIN') ON CONFLICT DO NOTHING;
