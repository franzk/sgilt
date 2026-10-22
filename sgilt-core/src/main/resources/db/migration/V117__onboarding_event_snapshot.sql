-- La session d'onboarding stocke désormais l'événement complet (coordonnées, données, rubriques
-- et demandes initiées) : elle n'est plus liée à un prestataire.
ALTER TABLE onboarding DROP COLUMN prestataire_id;

-- Les sessions en cours ont l'ancien format de données, illisible par l'activation : elles sont
-- annulées (le lien reçu par mail répond « déjà utilisé »), la personne recommence.
UPDATE onboarding SET state = 'CANCELLED' WHERE state IN ('OPEN', 'PENDING_CONFIRMATION');
