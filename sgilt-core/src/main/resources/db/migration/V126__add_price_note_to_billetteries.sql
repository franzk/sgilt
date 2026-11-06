-- Mention facultative affichée après le prix (« 60 € par place · Tout compris »), saisie librement
-- par l'organisateur. Null = rien après « par place ».
ALTER TABLE billetteries ADD COLUMN price_note VARCHAR(255);
