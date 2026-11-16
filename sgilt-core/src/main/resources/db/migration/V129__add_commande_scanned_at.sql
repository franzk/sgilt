-- Heure du scan de l'e-billet de la commande à l'entrée de l'événement (usage unique : le premier
-- scan fait entrer toutes ses places) : null tant qu'il n'a pas été scanné.
ALTER TABLE commandes ADD COLUMN scanned_at TIMESTAMP;
