package net.franzka.sgilt.core.onboarding.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import net.franzka.sgilt.core.evenement.dto.EvenementDto;

/**
 * Création d'un événement par un visiteur : ses coordonnées et l'événement
 * (données, rubriques, demandes initiées). C'est aussi la forme stockée dans la session
 * d'onboarding, quelle que soit la séquence d'entrée.
 */
public record InitOnboardingRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Email @Size(max = 255) String email,
        @Size(max = 30)
        @Pattern(regexp = "^(?:[\\s\\-.()/+]*\\d){7,15}[\\s\\-.()/+]*$", message = "Numéro de téléphone invalide")
        String telephone,
        @Valid @NotNull EvenementDto evenement
) {}
