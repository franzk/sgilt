CREATE TABLE reservations (
    id              UUID                NOT NULL PRIMARY KEY,
    evenement_id    UUID                NOT NULL REFERENCES evenements(id),
    prestataire_id  UUID                NOT NULL,
    status          reservation_status  NOT NULL,
    created_at      TIMESTAMP           NOT NULL
);
