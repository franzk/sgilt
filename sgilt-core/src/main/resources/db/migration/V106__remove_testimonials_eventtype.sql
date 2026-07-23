-- V106 : Suppression du champ `eventType` des témoignages (`testimonials`).
--
-- Ce champ est retiré du modèle : un témoignage ne porte plus que { author, text }. Les fiches
-- déjà éditées (testimonials éditables depuis la fiche pro) peuvent contenir des `eventType`
-- réels ; cette migration les retire des données existantes.
--
-- Idempotente : ne réécrit que les lignes dont un élément de `testimonials` porte encore la clé
-- `eventType`.

UPDATE prestataires
SET testimonials = (
    SELECT jsonb_agg(elem - 'eventType')
    FROM jsonb_array_elements(testimonials) AS elem
)
WHERE testimonials IS NOT NULL
  AND EXISTS (
      SELECT 1
      FROM jsonb_array_elements(testimonials) AS elem
      WHERE elem ? 'eventType'
  );
