-- Fusion de confirmation_tokens et tunnel_data en une seule entité onboarding.
-- L'id de la session est désormais porté dans le JWT set-password.

DROP TABLE tunnel_data;
DROP TABLE confirmation_tokens;
DROP TYPE confirmation_token_state;

CREATE TYPE onboarding_state AS ENUM (
    'OPEN',
    'PENDING_CONFIRMATION',
    'USED',
    'CANCELLED'
);

CREATE TABLE onboarding (
    id                              UUID             NOT NULL PRIMARY KEY,
    hmac_payload                    VARCHAR(255)     NOT NULL UNIQUE,
    email                           VARCHAR(255)     NOT NULL,
    state                           onboarding_state NOT NULL DEFAULT 'OPEN',
    expires_at                      TIMESTAMP        NOT NULL,
    confirmation_period_expires_at  TIMESTAMP,
    prestataire_id                  UUID             NOT NULL REFERENCES prestataires(id),
    data                            JSONB            NOT NULL,
    created_at                      TIMESTAMP        NOT NULL
);
