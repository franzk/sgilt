-- V110 : Ajout du statut clé-en-main (fiche provisionnée par l'équipe Sgilt, en construction,
-- jamais soumise par le prestataire, en attente de publication).

ALTER TYPE prestataire_status ADD VALUE 'WAITING_FOR_CREATION_SERVICE';
