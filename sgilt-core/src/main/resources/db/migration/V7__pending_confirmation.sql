ALTER TYPE confirmation_token_state ADD VALUE 'PENDING_CONFIRMATION' BEFORE 'USED';
ALTER TABLE confirmation_tokens ADD COLUMN confirmation_period_expires_at TIMESTAMP NULL;
