-- Normalisation des sous-catégories enregistrées avec leur libellé au lieu de leur clé.
--
-- Constaté en prod : 'Photographe', 'Photobooth', 'Jazz', 'Food Truck', 'Traiteur'
-- (souvent en doublon de la bonne clé), et 'Agence événementielle' (nouvelle sous-catégorie
-- services). Ces lignes ne matchent aucun filtre du front.
-- Même principe que V113 : insertion de la clé puis suppression du libellé, pour ne
-- jamais heurter la PK (prestataire_id, subcat_key).

INSERT INTO prestataires_sous_categories (prestataire_id, subcat_key)
SELECT s.prestataire_id, m.subcat_key
FROM prestataires_sous_categories s
JOIN (VALUES ('Photographe', 'photographe'),
             ('Photobooth', 'photobooth'),
             ('Jazz', 'jazz'),
             ('Food Truck', 'food-truck'),
             ('Traiteur', 'traiteur'),
             ('Agence événementielle', 'agence-evenementielle')) AS m (label, subcat_key)
  ON s.subcat_key = m.label
ON CONFLICT (prestataire_id, subcat_key) DO NOTHING;

DELETE FROM prestataires_sous_categories
WHERE subcat_key IN ('Photographe', 'Photobooth', 'Jazz', 'Food Truck', 'Traiteur', 'Agence événementielle');
