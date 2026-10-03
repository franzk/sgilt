-- Templates d'événement : un seul document JSON, tableau d'un template par type d'événement.
--
-- Un template donne les rubriques d'un type d'événement (clés explicites, libellés en i18n côté
-- front, ordre du tableau = ordre d'affichage) et ce que chacune regroupe : des catégories entières
-- et des sous-catégories isolées. Le back y lit toute règle qui en dépend (rubriques d'un nouvel
-- événement, rubrique d'une sous-catégorie…). Document unique : la table n'a qu'une ligne.

CREATE TABLE templates (
    id      SMALLINT NOT NULL PRIMARY KEY DEFAULT 1 CHECK (id = 1),
    content JSONB    NOT NULL
);

INSERT INTO templates (content) VALUES ('[
  {
    "type": "mariage",
    "rubriques": [
      { "key": "lieu",              "categories": ["lieu"],         "subcategories": [] },
      { "key": "restauration",      "categories": ["restauration"], "subcategories": [] },
      { "key": "musique-animation", "categories": ["musique"],      "subcategories": ["animation"] },
      { "key": "decoration",        "categories": [],               "subcategories": ["decoration"] },
      { "key": "hebergement",       "categories": [],               "subcategories": [] }
    ]
  }
]');

-- Rubriques propres à un événement, copiées de son template à la création (puis leur état vit
-- dans l'événement). Les événements existants n'en ont pas.
ALTER TABLE evenements ADD COLUMN rubriques JSONB NOT NULL DEFAULT '[]';

-- Changer une sous-catégorie de catégorie déplace ses prestataires avec elle.
ALTER TABLE prestataires DROP CONSTRAINT fk_prestataires_sous_categorie;
ALTER TABLE prestataires
    ADD CONSTRAINT fk_prestataires_sous_categorie
    FOREIGN KEY (subcat_key, category_key) REFERENCES sous_categories (key, category_key)
    ON UPDATE CASCADE;
