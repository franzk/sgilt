-- V12 : Migration des chemins d'images vers R2 (sgilt-media/uploads/).
-- Les fichiers ont été déplacés manuellement vers R2 sous uploads/ en conservant leur nom d'origine.
-- Cette migration remplace le préfixe /images/<sous-dossier>/ par uploads/ dans toutes les colonnes concernées.
-- Les URLs absolues (http/https) ne sont pas modifiées.

-- ── prestataires.hero_image ───────────────────────────────────────────────────
UPDATE prestataires
SET hero_image = 'uploads/' || regexp_replace(hero_image, '^/images/[^/]+/', '')
WHERE hero_image LIKE '/images/%';

-- ── prestataires.avatar ───────────────────────────────────────────────────────
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
