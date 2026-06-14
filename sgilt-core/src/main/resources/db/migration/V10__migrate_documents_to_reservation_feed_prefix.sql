-- V10 : Bascule du stockage des documents vers le bucket privé.
-- Les fichiers existants ont été déplacés manuellement de sgilt-media-staging/documents/
-- vers sgilt-documents-staging/reservation-feed/. Cette migration met à jour les
-- références en base en conséquence. Sans effet si aucune ligne ne correspond
-- (ex. prod, qui n'a jamais eu de données).
UPDATE reservation_feed
SET file_path = 'reservation-feed/' || substring(file_path FROM length('documents/') + 1)
WHERE type = 'DOCUMENT'
  AND file_path LIKE 'documents/%';
