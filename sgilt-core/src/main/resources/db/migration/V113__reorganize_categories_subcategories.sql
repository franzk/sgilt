-- Réorganisation du référentiel catégories / sous-catégories prestataire.
--
-- Avant : photo{photographe, photobooth}, services{Décoration, lieu, animation}
-- Après : lieu{salle}, services{decoration, animation, photographe, video, photobooth}
-- (musique et restauration inchangées)
--
-- Le référentiel vit dans sgilt-front (utils/constants.ts) ; en base seuls
-- prestataires.category_key et prestataires_sous_categories.subcat_key sont concernés.
-- Les sous-catégories sont supprimées/réinsérées (et non UPDATE) pour ne jamais
-- heurter la PK (prestataire_id, subcat_key).

-- 1. Lieu : les sous-catégories 'lieu' (front) et 'location-lieu' (seed) deviennent lieu.salle.
--    Le changement de catégorie doit précéder la migration des sous-catégories.
UPDATE prestataires
SET category_key = 'lieu'
WHERE id IN (SELECT prestataire_id
             FROM prestataires_sous_categories
             WHERE subcat_key IN ('lieu', 'location-lieu'));

INSERT INTO prestataires_sous_categories (prestataire_id, subcat_key)
SELECT prestataire_id, 'salle'
FROM prestataires_sous_categories
WHERE subcat_key IN ('lieu', 'location-lieu')
ON CONFLICT (prestataire_id, subcat_key) DO NOTHING;

DELETE FROM prestataires_sous_categories
WHERE subcat_key IN ('lieu', 'location-lieu');

-- 2. Décoration : correction de la casse de la clé.
INSERT INTO prestataires_sous_categories (prestataire_id, subcat_key)
SELECT prestataire_id, 'decoration'
FROM prestataires_sous_categories
WHERE subcat_key = 'Décoration'
ON CONFLICT (prestataire_id, subcat_key) DO NOTHING;

DELETE FROM prestataires_sous_categories
WHERE subcat_key = 'Décoration';

-- 3. Photo : la catégorie disparaît, ses prestataires (photographe, photobooth, vidéo)
--    rejoignent 'services'. Les clés de sous-catégories 'photographe' et 'photobooth'
--    sont conservées telles quelles.
UPDATE prestataires
SET category_key = 'services'
WHERE category_key = 'photo';
