package net.franzka.sgilt.core.utilisateur.domain;

public enum UtilisateurStatus {
    DRAFT,
    ACTIVE,
    /** Client créé depuis le BO, compte pas encore activé (passe ACTIVE à l'activation). */
    INVITED,
    ARCHIVED
}
