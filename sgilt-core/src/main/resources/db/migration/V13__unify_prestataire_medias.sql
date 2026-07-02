-- V13 : Unification des médias prestataires — hero_image + photos + youtube_id → medias (JSONB)
--
-- Nouveau format : tableau ordonné d'objets { "type": "IMAGE"|"YOUTUBE", "ref": string, "position": int }
-- Règle de construction :
--   hero_image         → { type: IMAGE,   ref: <hero_image>, position: 0 }
--   photos[0..N-1]     → { type: IMAGE,   ref: <url>,        position: 1..N }
--   youtube_id (si ≠ ∅) → { type: YOUTUBE, ref: <yt_id>,     position: N+1 }
-- Les ref sont conservées telles quelles (uploads/ relatif ou https:// absolu).
-- avatar est mis à NULL : il devient un champ autonome (fallback front → média pos-0).

-- ── 1. Ajout de la colonne ────────────────────────────────────────────────────

ALTER TABLE prestataires ADD COLUMN medias JSONB;

-- ── 2. Migration des données ──────────────────────────────────────────────────

UPDATE prestataires
SET medias = (
    -- position 0 : hero_image (toujours IMAGE, toujours premier)
    CASE
        WHEN hero_image IS NOT NULL
        THEN jsonb_build_array(jsonb_build_object('type', 'IMAGE', 'ref', hero_image, 'position', 0))
        ELSE '[]'::jsonb
    END
    ||
    -- positions 1..N : photos dans leur ordre d'origine
    COALESCE(
        (
            SELECT jsonb_agg(
                       jsonb_build_object('type', 'IMAGE', 'ref', elem #>> '{}', 'position', ord::int)
                       ORDER BY ord
                   )
            FROM jsonb_array_elements(COALESCE(photos, '[]'::jsonb)) WITH ORDINALITY t(elem, ord)
        ),
        '[]'::jsonb
    )
    ||
    -- dernière position : youtube_id si présent
    CASE
        WHEN youtube_id IS NOT NULL
        THEN jsonb_build_array(jsonb_build_object(
            'type',     'YOUTUBE',
            'ref',      youtube_id,
            'position', COALESCE(jsonb_array_length(photos), 0) + 1
        ))
        ELSE '[]'::jsonb
    END
);

-- ── 3. Reset avatar ───────────────────────────────────────────────────────────

UPDATE prestataires SET avatar = NULL;

-- ── 4. Suppression des anciennes colonnes ─────────────────────────────────────

ALTER TABLE prestataires DROP COLUMN hero_image;
ALTER TABLE prestataires DROP COLUMN photos;
ALTER TABLE prestataires DROP COLUMN youtube_id;
