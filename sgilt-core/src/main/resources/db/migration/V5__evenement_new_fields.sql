-- Alignement de la table evenements sur le nouveau modèle Evenement.
--   - statut → status (renommage pour correspondre au champ Java sans @Column(name))
--   - Ajout des colonnes descriptives issues du tunnel d'onboarding

ALTER TABLE evenements RENAME COLUMN statut TO status;

ALTER TABLE evenements ADD COLUMN event_type    VARCHAR(255);
ALTER TABLE evenements ADD COLUMN ambiance      VARCHAR(255);
ALTER TABLE evenements ADD COLUMN moment_cle    VARCHAR(255);
ALTER TABLE evenements ADD COLUMN description   TEXT;
ALTER TABLE evenements ADD COLUMN ville         VARCHAR(255);
ALTER TABLE evenements ADD COLUMN nb_invites    VARCHAR(255);
ALTER TABLE evenements ADD COLUMN lieu          VARCHAR(255);
ALTER TABLE evenements ADD COLUMN note_partagee VARCHAR(255);
