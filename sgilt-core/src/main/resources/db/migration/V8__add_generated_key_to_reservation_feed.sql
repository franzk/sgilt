ALTER TABLE reservation_feed ADD COLUMN generated_key VARCHAR(100);
ALTER TABLE reservation_feed ALTER COLUMN title DROP NOT NULL;
