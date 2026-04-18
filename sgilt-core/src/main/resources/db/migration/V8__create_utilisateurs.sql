CREATE TABLE utilisateurs (
    id          UUID         NOT NULL PRIMARY KEY,
    first_name  VARCHAR(255) NOT NULL,
    last_name   VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    avatar_url  VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL
);
