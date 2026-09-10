ALTER TABLE prestataires ADD COLUMN display_order INTEGER;

UPDATE prestataires p
SET display_order = sub.rn
FROM (
    SELECT id, ROW_NUMBER() OVER (ORDER BY random()) AS rn
    FROM prestataires
    WHERE deleted_at IS NULL
) sub
WHERE p.id = sub.id;
