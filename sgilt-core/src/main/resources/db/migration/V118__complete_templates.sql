-- Templates de tous les types d'événement (seul 'mariage' existait). Nouvelle rubrique
-- 'photo-video' (photographe, vidéo, photobooth). 'hebergement' sort du mariage (pas de prestataire
-- pour l'instant) : les événements déjà créés gardent leurs propres rubriques. Le type 'autre' n'a
-- pas de rubrique : l'événement se construit au fil des prestataires ajoutés.

UPDATE templates SET content = '[
  {
    "type": "mariage",
    "rubriques": [
      { "key": "lieu",              "categories": ["lieu"],         "subcategories": [] },
      { "key": "restauration",      "categories": ["restauration"], "subcategories": [] },
      { "key": "musique-animation", "categories": ["musique"],      "subcategories": ["animation"] },
      { "key": "decoration",        "categories": [],               "subcategories": ["decoration"] },
      { "key": "photo-video",       "categories": [],               "subcategories": ["photographe", "video", "photobooth"] }
    ]
  },
  {
    "type": "anniversaire",
    "rubriques": [
      { "key": "lieu",              "categories": ["lieu"],         "subcategories": [] },
      { "key": "restauration",      "categories": ["restauration"], "subcategories": [] },
      { "key": "musique-animation", "categories": ["musique"],      "subcategories": ["animation"] },
      { "key": "photo-video",       "categories": [],               "subcategories": ["photographe", "video", "photobooth"] }
    ]
  },
  {
    "type": "soiree_privee",
    "rubriques": [
      { "key": "lieu",              "categories": ["lieu"],         "subcategories": [] },
      { "key": "musique-animation", "categories": ["musique"],      "subcategories": ["animation"] },
      { "key": "restauration",      "categories": ["restauration"], "subcategories": [] },
      { "key": "photo-video",       "categories": [],               "subcategories": ["photographe", "video", "photobooth"] }
    ]
  },
  {
    "type": "fete_entreprise",
    "rubriques": [
      { "key": "lieu",              "categories": ["lieu"],         "subcategories": [] },
      { "key": "restauration",      "categories": ["restauration"], "subcategories": [] },
      { "key": "musique-animation", "categories": ["musique"],      "subcategories": ["animation"] },
      { "key": "photo-video",       "categories": [],               "subcategories": ["photographe", "video", "photobooth"] },
      { "key": "decoration",        "categories": [],               "subcategories": ["decoration"] }
    ]
  },
  {
    "type": "evenement_public",
    "rubriques": [
      { "key": "lieu",              "categories": ["lieu"],         "subcategories": [] },
      { "key": "restauration",      "categories": ["restauration"], "subcategories": [] },
      { "key": "musique-animation", "categories": ["musique"],      "subcategories": ["animation"] }
    ]
  },
  {
    "type": "autre",
    "rubriques": []
  }
]'
WHERE id = 1;
