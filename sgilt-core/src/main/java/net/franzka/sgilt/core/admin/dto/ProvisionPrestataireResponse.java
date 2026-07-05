package net.franzka.sgilt.core.admin.dto;

import java.util.UUID;

/**
 * DTO retourné après le provisionnement réussi d'un prestataire.
 */
public record ProvisionPrestataireResponse(
        UUID prestataireId,
        UUID utilisateurId,
        String slug
) {}
