package net.franzka.sgilt.core.evenement.billetterie.commande.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import net.franzka.sgilt.core.evenement.billetterie.commande.domain.Commande;

/**
 * Requête de création d'une commande de places, depuis l'écran de commande. Le prix n'y est pas :
 * c'est celui de la billetterie.
 *
 * @param slug      le slug de la page de l'événement (/e/{slug})
 * @param quantity  le nombre de places, au plus {@link Commande#MAX_PLACES_PER_COMMANDE}
 * @param firstName le prénom de l'acheteur
 * @param lastName  le nom de l'acheteur
 * @param email     l'e-mail de l'acheteur
 */
public record CreateCommandeRequest(
        @NotBlank String slug,
        @Positive @Max(Commande.MAX_PLACES_PER_COMMANDE) int quantity,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email
) {}
