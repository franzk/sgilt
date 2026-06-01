package net.franzka.sgilt.core.keycloak;

/**
 * Représentation minimale d'un rôle Keycloak pour l'API Admin (role-mappings).
 *
 * @param id   UUID du rôle dans le realm
 * @param name nom du rôle (ex : {@code USER}, {@code PRO})
 */
public record KeycloakRoleRepresentation(String id, String name) {}
