-- Code d'entrée de la commande, contenu de son QR (fait entrer tous ses billets), tiré au passage
-- PAID : null avant.
ALTER TABLE commandes ADD COLUMN code VARCHAR(255) UNIQUE;
