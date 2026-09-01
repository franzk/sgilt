-- V111 : Trace le flow à l'origine d'une fiche prestataire (clé-en-main / autonome), pour ne plus
-- avoir à l'inférer du statut courant lors du choix de notification (renvoi d'onboarding, publication).
-- Type générique (pas de "creation" dans le nom) pour accueillir d'autres flows futurs.

CREATE TYPE prestataire_flow AS ENUM (
    'CREATION_CLE_EN_MAIN',
    'CREATION_AUTONOME',
    'AUCUN'
);

-- les fiches existantes avant cette migration n'ont pas leur flow d'origine tracé
ALTER TABLE prestataires
    ADD COLUMN flow prestataire_flow NOT NULL DEFAULT 'AUCUN';
