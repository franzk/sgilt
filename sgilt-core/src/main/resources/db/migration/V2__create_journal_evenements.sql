CREATE TABLE journal_evenements (
    id            UUID      NOT NULL PRIMARY KEY,
    evenement_id  UUID      NOT NULL REFERENCES evenements(id),
    modifications JSONB     NOT NULL,
    created_at    TIMESTAMP NOT NULL
);

CREATE INDEX idx_journal_evenements_evenement_id ON journal_evenements(evenement_id);
