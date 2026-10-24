-- Deterministic, retry-safe expansion from legacy global roles. VENDOR is not
-- migrated as a platform role: active ShopMembership is the vendor capability.
INSERT INTO platform_role_grants (id, user_id, role, active)
SELECT md5('platform-role:user:' || id)::uuid, id, 'USER', TRUE FROM users
ON CONFLICT (user_id, role) DO NOTHING;

INSERT INTO platform_role_grants (id, user_id, role, active)
SELECT md5('platform-role:' || lower(role::text) || ':' || id)::uuid, id, role::text, TRUE
FROM users WHERE role IN ('ADMIN', 'SUPERADMIN')
ON CONFLICT (user_id, role) DO NOTHING;

INSERT INTO shop_memberships (id, shop_id, user_id, role, status)
SELECT md5('shop-membership:owner:' || s.id)::uuid, s.id, s.owner_user_id, 'OWNER',
       CASE WHEN s.status = 'APPROVED' THEN 'ACTIVE' ELSE 'REVOKED' END
FROM shops s
ON CONFLICT (shop_id, user_id, role) DO NOTHING;
