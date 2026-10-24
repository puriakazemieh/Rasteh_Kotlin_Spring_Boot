-- Expand-only identity model. Legacy users.role remains readable during migration.
CREATE TABLE platform_role_grants (
    id UUID PRIMARY KEY, user_id BIGINT NOT NULL REFERENCES users(id), role VARCHAR(32) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE, granted_by_user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), revoked_at TIMESTAMPTZ,
    UNIQUE(user_id, role)
);
CREATE TABLE shop_memberships (
    id UUID PRIMARY KEY, shop_id BIGINT NOT NULL REFERENCES shops(id), user_id BIGINT NOT NULL REFERENCES users(id),
    role VARCHAR(32) NOT NULL, status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    invited_by_user_id BIGINT REFERENCES users(id), created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), revoked_at TIMESTAMPTZ,
    UNIQUE(shop_id, user_id, role)
);
CREATE TABLE identity_audit_events (
    id UUID PRIMARY KEY, actor_user_id BIGINT REFERENCES users(id), subject_user_id BIGINT REFERENCES users(id),
    shop_id BIGINT REFERENCES shops(id), action VARCHAR(64) NOT NULL, detail_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE agent_assignments (
    id UUID PRIMARY KEY, agent_user_id BIGINT NOT NULL REFERENCES users(id), supervisor_user_id BIGINT REFERENCES users(id),
    cluster_key VARCHAR(120) NOT NULL, active BOOLEAN NOT NULL DEFAULT TRUE, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE agent_drafts (
    id UUID PRIMARY KEY, agent_user_id BIGINT NOT NULL REFERENCES users(id), assignment_id UUID NOT NULL REFERENCES agent_assignments(id),
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT', payload JSONB NOT NULL, consent_recorded_at TIMESTAMPTZ,
    submitted_at TIMESTAMPTZ, reviewed_by_user_id BIGINT REFERENCES users(id), reviewed_at TIMESTAMPTZ, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_shop_memberships_access ON shop_memberships(user_id, shop_id, status);
CREATE INDEX idx_agent_drafts_review ON agent_drafts(status, assignment_id);
