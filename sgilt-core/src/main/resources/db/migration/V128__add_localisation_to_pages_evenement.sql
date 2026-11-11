-- Localisation du lieu sur la page publique, saisie librement par l'organisateur : un lien de
-- partage Google Maps, une adresse ou un point GPS (« 48.5839,7.7455 »). La page publique en tire
-- son lien Google Maps. Null = pas de lien.
ALTER TABLE pages_evenement ADD COLUMN localisation VARCHAR(255);
