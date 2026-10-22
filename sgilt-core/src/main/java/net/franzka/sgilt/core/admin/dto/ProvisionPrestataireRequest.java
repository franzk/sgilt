package net.franzka.sgilt.core.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de la requête admin de provisionnement d'un prestataire.
 * {@code subcat} est la clé de la sous-catégorie du prestataire, qui doit appartenir à {@code category}.
 * {@code cleEnMain} distingue les deux flows de création : {@code true} pour le flow clé-en-main
 * (l'équipe Sgilt construit la fiche en impersonation, aucun mail envoyé avant publication),
 * {@code false} pour le flow autonome (comportement historique — mail d'activation envoyé immédiatement).
 */
public record ProvisionPrestataireRequest(
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String slug,
        @NotBlank String prestataireName,
        @NotBlank String category,
        @NotBlank String subcat,
        boolean cleEnMain
) {}
