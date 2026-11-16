package net.franzka.sgilt.core.evenement.billetterie.commande.dto;

import net.franzka.sgilt.core.evenement.billetterie.commande.domain.ScanStatus;

/**
 * Résultat du scan d'un e-billet à l'entrée d'un événement. Toujours renvoyé en 200 : chaque
 * statut est une issue normale du scan, pas une erreur.
 *
 * @param status   le résultat
 * @param commande la commande de l'e-billet ; null pour UNKNOWN (rien d'une commande d'un autre
 *                 événement n'est montré)
 */
public record ScanResultDto(
        ScanStatus status,
        ScannedCommandeDto commande
) {}
