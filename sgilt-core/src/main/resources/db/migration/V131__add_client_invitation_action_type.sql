-- V131 : Ajout du type de jeton d'action CLIENT_INVITATION — lien d'activation envoyé depuis le BO
-- à un client invité (deal conclu hors Sgilt), pour qu'il choisisse son mot de passe.

ALTER TYPE action_type ADD VALUE 'CLIENT_INVITATION';
