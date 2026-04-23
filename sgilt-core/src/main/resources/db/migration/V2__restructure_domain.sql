-- Restructuration du schéma pour aligner sur le modèle Java :
--   - evenement_status et reservation_status : valeurs renommées/enrichies
--   - calendrier_source, utilisateur_status : nouveaux types enum
--   - utilisateurs : telephone → phone, + status, + deleted_at
--   - evenements : rebuild complet (nouveau modèle événement utilisateur)
--   - reservations : rebuild complet (utilisateur_id, date, updated_at ; prestataire_id FK)
--   - Nouvelles tables : sous_categories, prestataires, prestataires_sous_categories,
--                        reservation_feed, calendrier_prestataire

-- ── 1. Drop des tables dans l'ordre des dépendances FK ───────────────────────
DROP TABLE confirmation_tokens;
DROP TABLE reservations;
DROP TABLE evenements;

-- ── 2. Drop des anciens types enum ───────────────────────────────────────────
DROP TYPE evenement_status;
DROP TYPE reservation_status;

-- ── 3. Nouveaux types enum ────────────────────────────────────────────────────
CREATE TYPE evenement_status AS ENUM (
    'DRAFT',
    'ACTIVE',
    'DONE',
    'CANCELED'
);

CREATE TYPE reservation_status AS ENUM (
    'DRAFT',
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

-- ── 4. Modification de utilisateurs ──────────────────────────────────────────
ALTER TABLE utilisateurs RENAME COLUMN telephone TO phone;
ALTER TABLE utilisateurs ADD COLUMN status utilisateur_status NOT NULL DEFAULT 'DRAFT';
ALTER TABLE utilisateurs ADD COLUMN deleted_at TIMESTAMP;

-- ── 5. sous_categories ────────────────────────────────────────────────────────
CREATE TABLE sous_categories (
    id            UUID         NOT NULL PRIMARY KEY,
    category_key  VARCHAR(255) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    "order"       INTEGER      NOT NULL,
    active        BOOLEAN      NOT NULL
);

-- ── 6. prestataires ───────────────────────────────────────────────────────────
CREATE TABLE prestataires (
    id                UUID         NOT NULL PRIMARY KEY,
    utilisateur_id    UUID         NOT NULL UNIQUE REFERENCES utilisateurs(id),
    name              VARCHAR(255) NOT NULL,
    slug              VARCHAR(255) NOT NULL UNIQUE,
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

-- ── 7. prestataires_sous_categories ──────────────────────────────────────────
CREATE TABLE prestataires_sous_categories (
    prestataire_id    UUID NOT NULL REFERENCES prestataires(id),
    sous_categorie_id UUID NOT NULL REFERENCES sous_categories(id),
    PRIMARY KEY (prestataire_id, sous_categorie_id)
);

-- ── 8. evenements (nouveau modèle) ────────────────────────────────────────────
CREATE TABLE evenements (
    id              UUID             NOT NULL PRIMARY KEY,
    utilisateur_id  UUID             NOT NULL REFERENCES utilisateurs(id),
    name            VARCHAR(255)     NOT NULL,
    date            DATE             NOT NULL,
    cover_url       VARCHAR(255),
    statut          evenement_status NOT NULL,
    created_at      TIMESTAMP        NOT NULL
);

-- ── 9. reservations (nouveau modèle) ─────────────────────────────────────────
CREATE TABLE reservations (
    id              UUID               NOT NULL PRIMARY KEY,
    evenement_id    UUID               NOT NULL REFERENCES evenements(id),
    prestataire_id  UUID               NOT NULL REFERENCES prestataires(id),
    utilisateur_id  UUID               NOT NULL REFERENCES utilisateurs(id),
    date            DATE               NOT NULL,
    status          reservation_status NOT NULL,
    created_at      TIMESTAMP          NOT NULL,
    updated_at      TIMESTAMP          NOT NULL
);

-- ── 10. reservation_feed (single-table inheritance : NOTE, DOCUMENT) ──────────
CREATE TABLE reservation_feed (
    id              UUID         NOT NULL PRIMARY KEY,
    type            VARCHAR(50)  NOT NULL,
    reservation_id  UUID         NOT NULL REFERENCES reservations(id),
    utilisateur_id  UUID         REFERENCES utilisateurs(id),
    prestataire_id  UUID         REFERENCES prestataires(id),
    title           VARCHAR(255) NOT NULL,
    hidden          BOOLEAN      NOT NULL,
    created_at      TIMESTAMP    NOT NULL,
    deleted_at      TIMESTAMP,
    -- Note
    content         TEXT,
    -- Document
    file_name       VARCHAR(255),
    url             VARCHAR(255),
    mime_type       VARCHAR(255)
);

-- ── 11. calendrier_prestataire ────────────────────────────────────────────────
CREATE TABLE calendrier_prestataire (
    id              UUID              NOT NULL PRIMARY KEY,
    prestataire_id  UUID              NOT NULL REFERENCES prestataires(id),
    date            DATE              NOT NULL,
    source          calendrier_source NOT NULL,
    reservation_id  UUID              REFERENCES reservations(id),
    gcal_event_id   VARCHAR(255),
    UNIQUE (prestataire_id, date)
);

-- ── 12. Index ─────────────────────────────────────────────────────────────────
CREATE INDEX idx_utilisateurs_email ON utilisateurs(email);
CREATE INDEX idx_reservations_evenement_id ON reservations(evenement_id);
CREATE INDEX idx_reservations_prestataire_id ON reservations(prestataire_id);
CREATE INDEX idx_reservation_feed_reservation_id ON reservation_feed(reservation_id);
CREATE INDEX idx_prestataires_sous_categories_sous_categorie_id ON prestataires_sous_categories(sous_categorie_id);
CREATE INDEX idx_calendrier_prestataire_prestataire_date ON calendrier_prestataire(prestataire_id, date);

-- ── 13. confirmation_tokens ───────────────────────────────────────────────────
CREATE TABLE confirmation_tokens (
    id                              UUID                     NOT NULL PRIMARY KEY,
    payload                         VARCHAR(255)             NOT NULL UNIQUE,
    email                           VARCHAR(255)             NOT NULL,
    reservation_id                  UUID                     REFERENCES reservations(id),
    state                           confirmation_token_state NOT NULL DEFAULT 'OPEN',
    confirmation_period_expires_at  TIMESTAMP,
    expires_at                      TIMESTAMP                NOT NULL,
    created_at                      TIMESTAMP                NOT NULL
);
