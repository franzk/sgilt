ALTER TABLE evenements
    ADD COLUMN event_type   VARCHAR(100),
    ADD COLUMN ambiance     VARCHAR(100),
    ADD COLUMN moment_cle   VARCHAR(100),
    ADD COLUMN description  TEXT,
    ADD COLUMN date         DATE,
    ADD COLUMN ville        VARCHAR(255),
    ADD COLUMN nb_invites   VARCHAR(50),
    ADD COLUMN lieu         TEXT,
    ADD COLUMN telephone    VARCHAR(50);
