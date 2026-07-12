-- ── Types enum ────────────────────────────────────────────────────────────────

CREATE TYPE notification_type AS ENUM (
    'STATE_CHANGE',
    'NEW_NOTE',
    'NEW_DOCUMENT',
    'NEW_REQUEST'
);

-- ── Tables ────────────────────────────────────────────────────────────────────

CREATE TABLE notifications (
    id                UUID               NOT NULL PRIMARY KEY,
    recipient_email   VARCHAR(255)       NOT NULL,
    recipient_user_id UUID,
    type              notification_type  NOT NULL,
    title             VARCHAR(255)       NOT NULL,
    body              TEXT,
    href              VARCHAR(255)       NOT NULL,
    is_read           BOOLEAN            NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMPTZ        NOT NULL
);

CREATE INDEX idx_notifications_recipient_email ON notifications (recipient_email, created_at DESC);
