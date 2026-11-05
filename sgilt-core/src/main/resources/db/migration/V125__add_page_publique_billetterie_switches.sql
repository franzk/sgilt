-- Interrupteurs du back-office : l'équipe Sgilt active la page publique et la billetterie d'un
-- événement, que l'organisateur administre ensuite. Couper un interrupteur ne supprime rien : la
-- page et la billetterie restent en base, et reviennent s'il est rallumé. Pas de billetterie sans
-- page (où acheter les billets).
ALTER TABLE evenements ADD COLUMN page_publique_enabled BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE evenements ADD COLUMN billetterie_enabled BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE evenements ADD CONSTRAINT evenements_billetterie_requires_page_publique
    CHECK (NOT billetterie_enabled OR page_publique_enabled);

-- Publication de la page : null = brouillon, introuvable sur /e/{slug}. La première publication
-- verrouille le slug (les liens partagés ne doivent pas casser).
ALTER TABLE pages_evenement ADD COLUMN published_at TIMESTAMP;
ALTER TABLE pages_evenement ADD COLUMN first_published_at TIMESTAMP;

-- Reprise : les pages existantes étaient en ligne, elles restent publiées, et leurs événements
-- gardent page et billetterie actives.
UPDATE pages_evenement SET published_at = created_at, first_published_at = created_at;
UPDATE evenements SET page_publique_enabled = true
    WHERE id IN (SELECT evenement_id FROM pages_evenement);
UPDATE evenements SET billetterie_enabled = true
    WHERE id IN (SELECT evenement_id FROM billetteries) AND page_publique_enabled;
