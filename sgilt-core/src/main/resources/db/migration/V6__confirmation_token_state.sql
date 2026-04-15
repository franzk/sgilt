CREATE TYPE confirmation_token_state AS ENUM ('OPEN', 'USED', 'CANCELLED');
ALTER TABLE confirmation_tokens ADD COLUMN state confirmation_token_state NOT NULL DEFAULT 'OPEN';
ALTER TABLE confirmation_tokens DROP COLUMN used;
