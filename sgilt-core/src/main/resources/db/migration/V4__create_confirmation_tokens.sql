CREATE TABLE confirmation_tokens (
    id              UUID         NOT NULL PRIMARY KEY,
    jti             VARCHAR(255) NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL,
    reservation_id  UUID         NOT NULL REFERENCES reservations(id),
    used            BOOLEAN      NOT NULL DEFAULT FALSE,
    expires_at      TIMESTAMP    NOT NULL,
    created_at      TIMESTAMP    NOT NULL
);
