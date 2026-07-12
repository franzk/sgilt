package net.franzka.sgilt.core.prestataire.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO d'une fiche prestataire dont l'onboarding est en attente — le lien envoyé par email n'a
 * pas encore été cliqué (le token d'action correspondant existe toujours en base).
 */
public record PrestataireOnboardingPendingDto(
        UUID prestataireId,
        String prestataireName,
        String email,
        LocalDateTime linkSentAt,
        LocalDateTime linkExpiresAt
) {}
