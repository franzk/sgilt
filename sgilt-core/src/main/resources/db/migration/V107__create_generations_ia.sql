-- V107 : Table de génération IA de fiche prestataire (résultat brut + quota)
--
-- Une seule ligne par prestataire, créée à la première génération puis mise à jour en place à
-- chaque génération suivante (pas d'historique/versioning).

CREATE TABLE generations_ia (
    id                        UUID         NOT NULL PRIMARY KEY,
    prestataire_id            UUID         NOT NULL UNIQUE REFERENCES prestataires(id),
    last_generation           JSONB,
    last_generation_date_time TIMESTAMP,
    tries_left                INTEGER      NOT NULL DEFAULT 3,
    source_url                VARCHAR(255)
);
