-- V130 : Ajout du statut INVITED — client créé depuis le BO (deal conclu hors Sgilt) avec son
-- événement et sa réservation confirmée, qui n'a pas encore activé son compte. Il passe ACTIVE
-- à l'activation.

ALTER TYPE utilisateur_status ADD VALUE 'INVITED';
