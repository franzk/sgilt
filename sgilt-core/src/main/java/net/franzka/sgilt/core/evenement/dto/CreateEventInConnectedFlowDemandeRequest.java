package net.franzka.sgilt.core.evenement.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Flow connecté — création d'un événement par demande unique (fiche d'un prestataire), pour un utilisateur
 * authentifié : les champs du tunnel. Le back en construit l'événement complet (rubriques du
 * template, demande rangée dans la sienne). Équivalent de
 * {@link net.franzka.sgilt.core.onboarding.dto.InitOnboardingDemandeRequest} sans les coordonnées
 * (l'utilisateur est connu via le JWT).
 */
public record CreateEventInConnectedFlowDemandeRequest(
        @NotNull UUID prestataireId,
        String eventType,
        String ambiance,
        String momentCle,
        String description,
        LocalDate date,
        String ville,
        String nbInvites,
        String lieu,
        String prestataireMessage
) {}
