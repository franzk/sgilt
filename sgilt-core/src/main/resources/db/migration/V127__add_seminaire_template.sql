-- Template du nouveau type d'événement 'seminaire' : lieu, restauration et une nouvelle rubrique
-- 'animation' (la sous-catégorie animation seule, sans la musique : un séminaire est une journée
-- hors des locaux, pas une soirée). Ajouté en fin de document, les autres templates sont inchangés.

UPDATE templates SET content = content || '[
  {
    "type": "seminaire",
    "rubriques": [
      { "key": "lieu",         "categories": ["lieu"],         "subcategories": [] },
      { "key": "restauration", "categories": ["restauration"], "subcategories": [] },
      { "key": "animation",    "categories": [],               "subcategories": ["animation"] }
    ]
  }
]'::jsonb
WHERE id = 1;
