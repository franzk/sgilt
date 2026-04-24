-- ConfirmationToken : suppression de reservation_id (relation portée par TunnelData)
-- TunnelData : nouveau snapshot des données du tunnel, lié au token de confirmation

ALTER TABLE confirmation_tokens DROP COLUMN reservation_id;

CREATE TABLE tunnel_data (
    id                      UUID      NOT NULL PRIMARY KEY,
    confirmation_token_id   UUID      NOT NULL UNIQUE REFERENCES confirmation_tokens(id),
    prestataire_id          UUID      NOT NULL REFERENCES prestataires(id),
    data                    JSONB     NOT NULL,
    created_at              TIMESTAMP NOT NULL
);
