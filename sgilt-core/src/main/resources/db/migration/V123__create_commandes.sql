-- Commande de billets d'une billetterie. Créée PENDING à la validation de l'écran de commande :
-- ses places sont bloquées jusqu'à expires_at (au-delà, une commande non payée ne compte plus et
-- ses places se libèrent d'elles-mêmes, sans tâche planifiée). PAID au paiement.
CREATE TYPE commande_status AS ENUM (
    'PENDING',
    'PAID',
    'CANCELLED'
);

CREATE TABLE commandes (
    id               UUID            NOT NULL PRIMARY KEY,
    billetterie_id   UUID            NOT NULL REFERENCES billetteries (id),
    quantity         INTEGER         NOT NULL CHECK (quantity > 0),
    -- Prix d'un billet à la commande, en centimes d'euro : figé, il ne suit pas la billetterie.
    unit_price_cents INTEGER         NOT NULL CHECK (unit_price_cents >= 0),
    buyer_first_name VARCHAR(255)    NOT NULL,
    buyer_last_name  VARCHAR(255)    NOT NULL,
    buyer_email      VARCHAR(255)    NOT NULL,
    status           commande_status NOT NULL,
    created_at       TIMESTAMP       NOT NULL,
    expires_at       TIMESTAMP       NOT NULL
);

CREATE INDEX idx_commandes_billetterie_id ON commandes (billetterie_id);
