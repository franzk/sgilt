-- Envoi de plusieurs documents : les documents déposés ensemble partagent un upload_id (généré par
-- le front), qui les regroupe en une seule carte du fil et une seule notification. Null pour les notes
-- et pour les documents antérieurs (chacun reste un envoi d'un seul fichier).
ALTER TABLE reservation_feed ADD COLUMN upload_id UUID;

CREATE INDEX idx_reservation_feed_upload_id ON reservation_feed(upload_id) WHERE upload_id IS NOT NULL;
