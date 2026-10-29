-- Horaires d'un événement (facultatifs) : heures locales du lieu, le jour de sa date ; une fin
-- avant le début tombe le lendemain.
ALTER TABLE evenements ADD COLUMN start_time TIME;
ALTER TABLE evenements ADD COLUMN end_time TIME;

-- Page d'un événement : sous-feature de l'événement, sa face publique (/e/{slug}). Un événement en
-- a une au plus. Le titre, la date, les horaires, l'image, le lieu et la ville restent ceux de
-- l'événement ; ici, seulement ce qui est propre à la page.
CREATE TABLE pages_evenement (
    id             UUID         NOT NULL PRIMARY KEY,
    evenement_id   UUID         NOT NULL UNIQUE REFERENCES evenements (id),
    slug           VARCHAR(255) NOT NULL UNIQUE,
    tag            VARCHAR(255),
    subtitle       VARCHAR(255),
    section_title  VARCHAR(255),
    description    TEXT,
    -- Avant l'achat (écran de commande).
    important_info JSONB        NOT NULL DEFAULT '[]',
    -- Après l'achat (confirmation).
    practical_info JSONB        NOT NULL DEFAULT '[]',
    -- Points forts de la page : [{ "icon", "label", "sublabel" }].
    pictos         JSONB        NOT NULL DEFAULT '[]',
    created_at     TIMESTAMP    NOT NULL
);

-- Billetterie : feature de l'événement, une au plus. Elle n'a de sens qu'avec une page (où acheter
-- les billets), règle portée par le service. Les places restantes ne sont pas stockées : elles se
-- déduisent de la jauge et des billets vendus ou bloqués.
CREATE TYPE billetterie_status AS ENUM (
    'NOT_OPEN',
    'OPEN',
    'CLOSED'
);

CREATE TABLE billetteries (
    id               UUID               NOT NULL PRIMARY KEY,
    evenement_id     UUID               NOT NULL UNIQUE REFERENCES evenements (id),
    status           billetterie_status NOT NULL,
    -- Prix d'un billet, en centimes d'euro.
    unit_price_cents INTEGER            NOT NULL CHECK (unit_price_cents >= 0),
    -- Nombre total de places mises en vente.
    capacity         INTEGER            NOT NULL CHECK (capacity >= 0),
    created_at       TIMESTAMP          NOT NULL
);
