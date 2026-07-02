-- V102 : Correction des chemins d'images du seed V101 vers R2.
-- V101 insère encore les anciens chemins /images/prestataires/... ; ce script
-- les normalise en uploads/... après insertion, de la même façon que V12 le fait
-- pour les bases existantes.

UPDATE prestataires
SET hero_image = 'uploads/' || regexp_replace(hero_image, '^/images/[^/]+/', '')
WHERE hero_image LIKE '/images/%';

UPDATE prestataires
SET avatar = 'uploads/' || regexp_replace(avatar, '^/images/[^/]+/', '')
WHERE avatar LIKE '/images/%';

-- ── prestataires.photos (JSONB array) ────────────────────────────────────────
UPDATE prestataires
SET photos = (
    SELECT jsonb_agg(
                   CASE
                       WHEN elem #>> '{}' LIKE '/images/%'
                       THEN to_jsonb('uploads/' || regexp_replace(elem #>> '{}', '^/images/[^/]+/', ''))
            ELSE elem
        END
           )
    FROM jsonb_array_elements(photos) elem
)
WHERE photos IS NOT NULL
  AND EXISTS (
    SELECT 1 FROM jsonb_array_elements_text(photos) t WHERE t LIKE '/images/%'
);
