-- V14 : Table des tokens d'action (étape 1/4 — onboarding prestataire)

CREATE TYPE action_type AS ENUM (
    'PRESTATAIRE_ONBOARDING'
);

CREATE TABLE action_tokens (
    id           UUID         NOT NULL PRIMARY KEY,
    hmac_payload VARCHAR(255) NOT NULL UNIQUE,
    type         action_type  NOT NULL,
    payload      JSONB        NOT NULL,
    expires_at   TIMESTAMP    NOT NULL,
    created_at   TIMESTAMP    NOT NULL
);
