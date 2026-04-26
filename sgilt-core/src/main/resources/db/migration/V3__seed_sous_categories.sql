-- V3 : Données de référence — sous-catégories (musique, restauration, photo, services)
-- Les IDs suivent la convention ee0000XX pour correspondre aux subcats du front-end.

INSERT INTO sous_categories (id, category_key, name, "order", active) VALUES
  ('ee000021-0000-0000-0000-000000000000', 'musique',      'DJ & Animation',          1, true),
  ('ee000022-0000-0000-0000-000000000000', 'musique',      'Groupe Pop-Rock / Rock',  2, true),
  ('ee000023-0000-0000-0000-000000000000', 'musique',      'Jazz & Musique Live',     3, true),
  ('ee000031-0000-0000-0000-000000000000', 'restauration', 'Traiteur & Cocktail',     1, true),
  ('ee000032-0000-0000-0000-000000000000', 'restauration', 'Food Truck',              2, true),
  ('ee000041-0000-0000-0000-000000000000', 'photo',        'Photographe',             1, true),
  ('ee000043-0000-0000-0000-000000000000', 'photo',        'Photobooth',              3, true),
  ('ee000052-0000-0000-0000-000000000000', 'services',     'Location de lieu',        2, true);
