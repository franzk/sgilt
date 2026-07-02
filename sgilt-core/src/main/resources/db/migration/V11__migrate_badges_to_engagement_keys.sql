-- V11 : Conversion des badges JSONB ({icon, label, description, color}) en tableau de clés Engagement.
-- Le mapping est basé sur le champ `description` qui est cohérent sur les 15 prestataires seed.
-- Guard : on ne touche que les lignes en ancien format (premier élément est un objet avec "icon").
-- Sur un fresh install avec V101 déjà mis à jour, cette migration est sans effet.

UPDATE prestataires
SET badges = (
    SELECT jsonb_agg(
        CASE elem->>'description'
            WHEN 'Réponse sous 48h'           THEN '"REPONSE_48H"'::jsonb
            WHEN 'Prestation adaptable'        THEN '"ADAPTABLE"'::jsonb
            WHEN 'Accompagnement personnalisé' THEN '"ACCOMPAGNEMENT"'::jsonb
            WHEN 'Autonome et équipé'          THEN '"EQUIPE"'::jsonb
            WHEN 'Interlocuteur unique'        THEN '"INTERLOCUTEUR_UNIQUE"'::jsonb
            WHEN 'Éco-responsable'             THEN '"ECORESPONSABLE"'::jsonb
        END
    )
    FROM jsonb_array_elements(badges) AS elem
)
WHERE badges IS NOT NULL
  AND (badges -> 0 -> 'icon') IS NOT NULL;
