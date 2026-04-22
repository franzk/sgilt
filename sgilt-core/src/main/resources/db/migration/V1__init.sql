CREATE TYPE evenement_status AS ENUM (
    'DRAFT', 'ACTIVE', 'ARCHIVE'
);

CREATE TYPE reservation_status AS ENUM (
    'DRAFT', 'NOUVELLE', 'EN_DISCUSSION',
    'CONFIRMEE', 'REALISEE', 'REFUSEE',
    'ANNULEE', 'ANNULEE_POST_CONFIRMATION'
);

CREATE TYPE confirmation_token_state AS ENUM (
    'OPEN', 'PENDING_CONFIRMATION', 'USED', 'CANCELLED'
);

CREATE TABLE utilisateurs (
    id          UUID         NOT NULL PRIMARY KEY,
    first_name  VARCHAR(255) NOT NULL,
    last_name   VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    avatar_url  VARCHAR(255),
    telephone   VARCHAR(50),
    created_at  TIMESTAMP    NOT NULL
);

CREATE TABLE evenements (
    id          UUID                NOT NULL PRIMARY KEY,
    first_name  VARCHAR(100)        NOT NULL,
    last_name   VARCHAR(100)        NOT NULL,
    email       VARCHAR(255)        NOT NULL,
    status      evenement_status    NOT NULL,
    event_type  VARCHAR(100),
    ambiance    VARCHAR(100),
    moment_cle  VARCHAR(100),
    description TEXT,
    date        DATE,
    ville       VARCHAR(255),
    nb_invites  VARCHAR(50),
    lieu        TEXT,
    telephone   VARCHAR(50),
    created_at  TIMESTAMP           NOT NULL
);

CREATE TABLE reservations (
    id                  UUID                NOT NULL PRIMARY KEY,
    evenement_id        UUID                NOT NULL REFERENCES evenements(id),
    prestataire_id      UUID                NOT NULL,
    status              reservation_status  NOT NULL,
    prestataire_message TEXT,
    created_at          TIMESTAMP           NOT NULL
);

CREATE TABLE confirmation_tokens (
    id                              UUID                        NOT NULL PRIMARY KEY,
    payload                         VARCHAR(255)                NOT NULL UNIQUE,
    email                           VARCHAR(255)                NOT NULL,
    reservation_id                  UUID                        NOT NULL REFERENCES reservations(id),
    state                           confirmation_token_state    NOT NULL DEFAULT 'OPEN',
    confirmation_period_expires_at  TIMESTAMP,
    expires_at                      TIMESTAMP                   NOT NULL,
    created_at                      TIMESTAMP                   NOT NULL
);
