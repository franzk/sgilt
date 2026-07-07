package net.franzka.sgilt.core.keycloak;

/**
 * Représentation minimale d'un utilisateur Keycloak pour l'API Admin (recherche par email).
 *
 * @param id UUID de l'utilisateur dans le realm
 */
public record KeycloakUserRepresentation(String id) {}
