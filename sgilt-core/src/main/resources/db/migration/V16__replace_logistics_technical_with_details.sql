-- V16 : Remplacement de logistics + technical par details (JSONB) — le découpage logistique/technique
-- ne reflétait pas ce que les prestataires renseignent réellement (cf. intégration IA)
--
-- Nouveau format : tableau d'objets { "content": string, "category": string }
-- category ∈ FORMAT, PROCESS, LOGISTICS, DELIVERABLE, OTHER
-- Règle de reprise des données existantes :
--   logistics[i] → { content: "Logistique : " || logistics[i],           category: 'LOGISTICS' }
--   technical[i] → { content: "Information technique : " || technical[i], category: 'OTHER' }

-- ── 1. Ajout de la colonne ────────────────────────────────────────────────────

ALTER TABLE prestataires ADD COLUMN details JSONB;

-- ── 2. Migration des données ──────────────────────────────────────────────────

UPDATE prestataires
SET details = (
    COALESCE(
        (
            SELECT jsonb_agg(
                       jsonb_build_object(
                           'content', 'Logistique : ' || (elem #>> '{}'),
                           'category', 'LOGISTICS'
                       )
                   )
            FROM jsonb_array_elements(COALESCE(logistics, '[]'::jsonb)) AS elem
        ),
        '[]'::jsonb
    )
    ||
    COALESCE(
        (
            SELECT jsonb_agg(
                       jsonb_build_object(
                           'content', 'Information technique : ' || (elem #>> '{}'),
                           'category', 'OTHER'
                       )
                   )
            FROM jsonb_array_elements(COALESCE(technical, '[]'::jsonb)) AS elem
        ),
        '[]'::jsonb
    )
);

-- ── 3. Suppression des anciennes colonnes ─────────────────────────────────────

ALTER TABLE prestataires DROP COLUMN logistics;
ALTER TABLE prestataires DROP COLUMN technical;
