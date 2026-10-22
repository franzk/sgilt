-- Rubrique de chaque réservation, enregistrée : une réservation est rangée une fois, à son entrée,
-- et la lecture restitue ce rangement (le template ne sert plus qu'à ranger une entrée).
-- Cette migration range les réservations existantes avec la règle d'entrée :
--   1. la rubrique du template de l'événement qui regroupe la sous-catégorie du prestataire,
--      sinon celle qui regroupe sa catégorie ;
--   2. sinon, la même recherche dans tous les templates (catalogue des rubriques connues) ;
--   3. sinon 'autre'.
-- Les rubriques de l'événement doivent contenir toutes les rubriques de ses réservations.
-- Colonne nullable ici : elle passera NOT NULL quand le code la renseignera à la création.

ALTER TABLE reservations ADD COLUMN rubrique_key VARCHAR(100);

-- Rubriques de tous les templates, à plat, dans l'ordre du document.
CREATE TEMPORARY TABLE tmp_template_rubriques AS
SELECT t.position             AS template_position,
       t.template ->> 'type'  AS type,
       r.position             AS rubrique_position,
       r.rubrique ->> 'key'   AS key,
       r.rubrique -> 'categories'    AS categories,
       r.rubrique -> 'subcategories' AS subcategories
FROM templates,
     jsonb_array_elements(content) WITH ORDINALITY AS t (template, position),
     jsonb_array_elements(t.template -> 'rubriques') WITH ORDINALITY AS r (rubrique, position);

-- Type de template de chaque événement : sa clé, ou son libellé pour les anciens événements dont le
-- type est en texte libre ('Anniversaire'…).
CREATE TEMPORARY TABLE tmp_evenement_types AS
SELECT e.id, COALESCE(m.type, lower(e.event_type)) AS type
FROM evenements e
LEFT JOIN (VALUES ('mariage',           'mariage'),
                  ('anniversaire',      'anniversaire'),
                  ('soirée privée',     'soiree_privee'),
                  ('fête d''entreprise', 'fete_entreprise'),
                  ('événement public',  'evenement_public'),
                  ('autre',             'autre')) AS m (label, type)
       ON lower(e.event_type) = m.label;

-- 1. Les événements sans rubriques (antérieurs aux templates) reçoivent celles de leur template.
UPDATE evenements e
SET rubriques = (SELECT jsonb_agg(jsonb_build_object('key', tr.key) ORDER BY tr.rubrique_position)
                 FROM tmp_template_rubriques tr
                 WHERE tr.type = et.type)
FROM tmp_evenement_types et
WHERE et.id = e.id
  AND e.rubriques = '[]'::jsonb
  AND EXISTS (SELECT 1 FROM tmp_template_rubriques tr WHERE tr.type = et.type);

-- 2. Chaque réservation est rangée selon la règle d'entrée.
UPDATE reservations r
SET rubrique_key = COALESCE(
        (SELECT tr.key FROM tmp_template_rubriques tr
         WHERE tr.type = et.type AND tr.subcategories ? p.subcat_key
         ORDER BY tr.rubrique_position LIMIT 1),
        (SELECT tr.key FROM tmp_template_rubriques tr
         WHERE tr.type = et.type AND tr.categories ? p.category_key
         ORDER BY tr.rubrique_position LIMIT 1),
        (SELECT tr.key FROM tmp_template_rubriques tr
         WHERE tr.subcategories ? p.subcat_key
         ORDER BY tr.template_position, tr.rubrique_position LIMIT 1),
        (SELECT tr.key FROM tmp_template_rubriques tr
         WHERE tr.categories ? p.category_key
         ORDER BY tr.template_position, tr.rubrique_position LIMIT 1),
        'autre')
FROM prestataires p,
     tmp_evenement_types et
WHERE p.id = r.prestataire_id
  AND et.id = r.evenement_id;

-- Par sécurité : une réservation sans prestataire retrouvé va dans 'autre'.
UPDATE reservations SET rubrique_key = 'autre' WHERE rubrique_key IS NULL;

-- 3. Les rubriques qui reçoivent une réservation sans faire partie de l'événement y sont ajoutées
--    en fin (dans l'ordre d'arrivée de leur première réservation), 'autre' en dernier.
WITH missing AS (
    SELECT r.evenement_id, r.rubrique_key, MIN(r.created_at) AS first_created_at
    FROM reservations r
    JOIN evenements e ON e.id = r.evenement_id
    WHERE NOT EXISTS (SELECT 1
                      FROM jsonb_array_elements(e.rubriques) AS existing (rubrique)
                      WHERE existing.rubrique ->> 'key' = r.rubrique_key)
    GROUP BY r.evenement_id, r.rubrique_key
)
UPDATE evenements e
SET rubriques = e.rubriques || added.rubriques
FROM (SELECT evenement_id,
             jsonb_agg(jsonb_build_object('key', rubrique_key)
                       ORDER BY rubrique_key = 'autre', first_created_at) AS rubriques
      FROM missing
      GROUP BY evenement_id) AS added
WHERE added.evenement_id = e.id;

DROP TABLE tmp_template_rubriques;
DROP TABLE tmp_evenement_types;
