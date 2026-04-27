-- V1 : Schéma initial complet (squash de V1 à V5)

-- ── Types enum ────────────────────────────────────────────────────────────────

CREATE TYPE evenement_status AS ENUM (
    'ACTIVE',
    'DONE',
    'CANCELED'
);

CREATE TYPE reservation_status AS ENUM (
    'NEW',
    'IN_DISCUSSION',
    'CONFIRMED',
    'DONE',
    'REFUSED_PRE_CONTACT',
    'REFUSED_POST_CONTACT',
    'CANCELED_BY_CLIENT_PRE_CONTACT',
    'CANCELED_BY_CLIENT_POST_CONTACT',
    'CANCELED_POST_CONFIRMATION'
);

CREATE TYPE calendrier_source AS ENUM (
    'RESERVATION',
    'MANUAL',
    'GCAL'
);

CREATE TYPE utilisateur_status AS ENUM (
    'DRAFT',
    'ACTIVE',
    'ARCHIVED'
);

CREATE TYPE onboarding_state AS ENUM (
    'OPEN',
    'PENDING_CONFIRMATION',
    'USED',
    'CANCELLED'
);

-- ── Tables ────────────────────────────────────────────────────────────────────

CREATE TABLE utilisateurs (
    id          UUID               NOT NULL PRIMARY KEY,
    first_name  VARCHAR(255)       NOT NULL,
    last_name   VARCHAR(255)       NOT NULL,
    email       VARCHAR(255)       NOT NULL UNIQUE,
    avatar_url  VARCHAR(255),
    phone       VARCHAR(50),
    status      utilisateur_status NOT NULL DEFAULT 'DRAFT',
    created_at  TIMESTAMP          NOT NULL,
    deleted_at  TIMESTAMP
);

CREATE TABLE prestataires (
    id                UUID         NOT NULL PRIMARY KEY,
    utilisateur_id    UUID         NOT NULL UNIQUE REFERENCES utilisateurs(id),
    name              VARCHAR(255) NOT NULL,
    slug              VARCHAR(255) NOT NULL UNIQUE,
    category_key      VARCHAR(100) NOT NULL DEFAULT '',
    youtube_id        VARCHAR(255),
    baseline          VARCHAR(255),
    hero_image        VARCHAR(255),
    avatar            VARCHAR(255),
    short_description TEXT,
    photos            JSONB,
    badges            JSONB,
    offerings         JSONB,
    identity          JSONB,
    budget            JSONB,
    testimonials      JSONB,
    logistics         JSONB,
    technical         JSONB,
    faq               JSONB,
    created_at        TIMESTAMP    NOT NULL,
    deleted_at        TIMESTAMP
);

CREATE TABLE prestataires_sous_categories (
    prestataire_id UUID         NOT NULL REFERENCES prestataires(id),
    subcat_key     VARCHAR(100) NOT NULL,
    PRIMARY KEY (prestataire_id, subcat_key)
);

CREATE TABLE evenements (
    id            UUID             NOT NULL PRIMARY KEY,
    utilisateur_id UUID            NOT NULL REFERENCES utilisateurs(id),
    name          VARCHAR(255)     NOT NULL,
    date          DATE             NOT NULL,
    cover_url     VARCHAR(255),
    status        evenement_status NOT NULL,
    event_type    VARCHAR(255),
    ambiance      VARCHAR(255),
    moment_cle    VARCHAR(255),
    description   TEXT,
    ville         VARCHAR(255),
    nb_invites    VARCHAR(255),
    lieu          VARCHAR(255),
    note_partagee VARCHAR(255),
    created_at    TIMESTAMP        NOT NULL
);

CREATE TABLE reservations (
    id             UUID               NOT NULL PRIMARY KEY,
    evenement_id   UUID               NOT NULL REFERENCES evenements(id),
    prestataire_id UUID               NOT NULL REFERENCES prestataires(id),
    utilisateur_id UUID               NOT NULL REFERENCES utilisateurs(id),
    date           DATE               NOT NULL,
    status         reservation_status NOT NULL,
    created_at     TIMESTAMP          NOT NULL,
    updated_at     TIMESTAMP          NOT NULL
);

CREATE TABLE reservation_feed (
    id             UUID         NOT NULL PRIMARY KEY,
    type           VARCHAR(50)  NOT NULL,
    reservation_id UUID         NOT NULL REFERENCES reservations(id),
    utilisateur_id UUID         REFERENCES utilisateurs(id),
    prestataire_id UUID         REFERENCES prestataires(id),
    title          VARCHAR(255) NOT NULL,
    hidden         BOOLEAN      NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    deleted_at     TIMESTAMP,
    -- Note
    content        TEXT,
    -- Document
    file_name      VARCHAR(255),
    url            VARCHAR(255),
    mime_type      VARCHAR(255)
);

CREATE TABLE calendrier_prestataire (
    id             UUID              NOT NULL PRIMARY KEY,
    prestataire_id UUID              NOT NULL REFERENCES prestataires(id),
    date           DATE              NOT NULL,
    source         calendrier_source NOT NULL,
    reservation_id UUID              REFERENCES reservations(id),
    gcal_event_id  VARCHAR(255),
    UNIQUE (prestataire_id, date)
);

CREATE TABLE onboarding (
    id                             UUID             NOT NULL PRIMARY KEY,
    hmac_payload                   VARCHAR(255)     NOT NULL UNIQUE,
    email                          VARCHAR(255)     NOT NULL,
    state                          onboarding_state NOT NULL DEFAULT 'OPEN',
    expires_at                     TIMESTAMP        NOT NULL,
    confirmation_period_expires_at TIMESTAMP,
    prestataire_id                 UUID             NOT NULL REFERENCES prestataires(id),
    data                           JSONB            NOT NULL,
    created_at                     TIMESTAMP        NOT NULL
);

-- ── Index ─────────────────────────────────────────────────────────────────────

CREATE INDEX idx_utilisateurs_email                        ON utilisateurs(email);
CREATE INDEX idx_prestataires_category_key                 ON prestataires(category_key);
CREATE INDEX idx_prestataires_sous_categories_subcat_key   ON prestataires_sous_categories(subcat_key);
CREATE INDEX idx_reservations_evenement_id                 ON reservations(evenement_id);
CREATE INDEX idx_reservations_prestataire_id               ON reservations(prestataire_id);
CREATE INDEX idx_reservation_feed_reservation_id           ON reservation_feed(reservation_id);
CREATE INDEX idx_calendrier_prestataire_prestataire_date   ON calendrier_prestataire(prestataire_id, date);
