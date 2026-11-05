package net.franzka.sgilt.core.evenement.dto;

/**
 * DTO des interrupteurs d'un événement dans le back-office admin : l'équipe Sgilt active sa page
 * publique et sa billetterie, que l'organisateur administre ensuite.
 *
 * @param pagePubliqueEnabled la page publique est activée
 * @param billetterieEnabled  la billetterie est activée ; jamais sans page publique
 */
public record AdminEvenementFeaturesDto(
        boolean pagePubliqueEnabled,
        boolean billetterieEnabled
) {}
