-- Référentiel catégories / sous-catégories prestataire en base.
--
-- Le référentiel est servi au front par GET /prestataires/categories : une nouvelle
-- sous-catégorie s'ajoute par INSERT, sans mise en production.
-- Un prestataire a exactement une sous-catégorie (prestataires.subcat_key). Sa catégorie
-- (prestataires.category_key) est conservée : la FK composite garantit qu'elle est celle de
-- sa sous-catégorie.

-- 1. Référentiel
CREATE TABLE categories (
    key      VARCHAR(100) NOT NULL PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    position INT          NOT NULL
);

CREATE TABLE sous_categories (
    key          VARCHAR(100) NOT NULL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    category_key VARCHAR(100) NOT NULL REFERENCES categories(key),
    position     INT          NOT NULL,
    UNIQUE (key, category_key)
);

INSERT INTO categories (key, name, position) VALUES
    ('musique',      'Musique',      1),
    ('restauration', 'Restauration', 2),
    ('lieu',         'Lieu',         3),
    ('services',     'Services',     4);

INSERT INTO sous_categories (key, name, category_key, position) VALUES
    ('dj',                    'DJ',                    'musique',      1),
    ('pop-rock',              'Pop/Rock',              'musique',      2),
    ('jazz',                  'Jazz',                  'musique',      3),
    ('traiteur',              'Traiteur',              'restauration', 1),
    ('food-truck',            'Food Truck',            'restauration', 2),
    ('salle',                 'Salle',                 'lieu',         1),
    ('decoration',            'Décoration',            'services',     1),
    ('animation',             'Animation',             'services',     2),
    ('photographe',           'Photographe',           'services',     3),
    ('video',                 'Vidéo',                 'services',     4),
    ('photobooth',            'Photobooth',            'services',     5),
    ('communication',         'Communication',         'services',     6),
    ('location-voiture',      'Location de voiture',   'services',     7),
    ('agence-evenementielle', 'Agence événementielle', 'services',     8);

-- 2. Une seule sous-catégorie par prestataire. La migration échoue si un prestataire en a
--    zéro ou plusieurs : le choix de la sous-catégorie à garder se fait à la main, pas ici.
DO $$
DECLARE
    invalid TEXT;
BEGIN
    SELECT string_agg(p.slug || ' (' || count_subcats || ')', ', ')
    INTO invalid
    FROM (SELECT p.id, count(s.subcat_key) AS count_subcats
          FROM prestataires p
          LEFT JOIN prestataires_sous_categories s ON s.prestataire_id = p.id
          GROUP BY p.id) c
    JOIN prestataires p ON p.id = c.id
    WHERE c.count_subcats <> 1;

    IF invalid IS NOT NULL THEN
        RAISE EXCEPTION 'Prestataires sans exactement une sous-catégorie : %', invalid;
    END IF;
END $$;

-- 3. La sous-catégorie doit exister dans le référentiel et appartenir à la catégorie du
--    prestataire. Même principe : la correction se fait à la main, la migration liste tout.
DO $$
DECLARE
    invalid TEXT;
BEGIN
    SELECT string_agg(p.slug || ' (' || p.category_key || ' / ' || s.subcat_key || ')', ', ')
    INTO invalid
    FROM prestataires p
    JOIN prestataires_sous_categories s ON s.prestataire_id = p.id
    LEFT JOIN sous_categories sc ON sc.key = s.subcat_key AND sc.category_key = p.category_key
    WHERE sc.key IS NULL;

    IF invalid IS NOT NULL THEN
        RAISE EXCEPTION 'Prestataires dont la sous-catégorie est absente du référentiel ou d''une autre catégorie : %', invalid;
    END IF;
END $$;

ALTER TABLE prestataires ADD COLUMN subcat_key VARCHAR(100);

UPDATE prestataires p
SET subcat_key = s.subcat_key
FROM prestataires_sous_categories s
WHERE s.prestataire_id = p.id;

ALTER TABLE prestataires ALTER COLUMN subcat_key SET NOT NULL;
ALTER TABLE prestataires ALTER COLUMN category_key DROP DEFAULT;
ALTER TABLE prestataires
    ADD CONSTRAINT fk_prestataires_sous_categorie
    FOREIGN KEY (subcat_key, category_key) REFERENCES sous_categories (key, category_key);

CREATE INDEX idx_prestataires_subcat_key ON prestataires(subcat_key);

DROP TABLE prestataires_sous_categories;
