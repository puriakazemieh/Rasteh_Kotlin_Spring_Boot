ALTER TABLE payments ADD COLUMN IF NOT EXISTS user_id BIGINT;
ALTER TABLE payments ADD COLUMN IF NOT EXISTS idempotency_key VARCHAR(100);

CREATE UNIQUE INDEX IF NOT EXISTS ux_payments_user_idempotency_key
    ON payments (user_id, idempotency_key)
    WHERE idempotency_key IS NOT NULL;
