-- V15 : Ajout du statut de fiche prestataire (state machine DRAFT / IN_REVIEW / PUBLISHED)

CREATE TYPE prestataire_status AS ENUM (
    'DRAFT',
    'IN_REVIEW',
    'PUBLISHED'
);

ALTER TABLE prestataires
    ADD COLUMN status prestataire_status NOT NULL DEFAULT 'DRAFT';

-- les fiches existantes avant cette migration sont considérées comme publiées
UPDATE prestataires SET status = 'PUBLISHED';
