-- V4 : Refactoring des sous-catégories
-- La table sous_categories est supprimée : les libellés sont gérés en constantes (front + back).
-- Le join table utilise maintenant une clé string simple.
-- La catégorie est stockée directement sur le prestataire (category_key).

DROP TABLE prestataires_sous_categories;
DROP TABLE sous_categories;

ALTER TABLE prestataires ADD COLUMN category_key VARCHAR(100) NOT NULL DEFAULT '';

CREATE TABLE prestataires_sous_categories (
    prestataire_id UUID         NOT NULL REFERENCES prestataires(id),
    subcat_key     VARCHAR(100) NOT NULL,
    PRIMARY KEY (prestataire_id, subcat_key)
);

CREATE INDEX idx_prestataires_category_key ON prestataires(category_key);
CREATE INDEX idx_prestataires_sous_categories_subcat_key ON prestataires_sous_categories(subcat_key);
