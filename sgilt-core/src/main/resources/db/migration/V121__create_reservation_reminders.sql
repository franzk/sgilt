-- Rappels automatiques envoyés pour une réservation : une ligne par rappel envoyé, de sorte qu'un
-- même rappel ne parte qu'une fois (contrainte unique) et qu'un nouveau rappel n'ajoute qu'une
-- valeur à l'enum, pas une colonne sur reservations.
CREATE TYPE reservation_reminder_type AS ENUM (
    'CONTACT_PENDING'
);

CREATE TABLE reservation_reminders (
    id             UUID                      NOT NULL PRIMARY KEY,
    reservation_id UUID                      NOT NULL REFERENCES reservations (id),
    type           reservation_reminder_type NOT NULL,
    sent_at        TIMESTAMP                 NOT NULL,
    CONSTRAINT uq_reservation_reminders_reservation_type UNIQUE (reservation_id, type)
);
