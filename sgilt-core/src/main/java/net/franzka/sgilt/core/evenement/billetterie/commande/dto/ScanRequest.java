package net.franzka.sgilt.core.evenement.billetterie.commande.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Requête de scan d'un e-billet à l'entrée d'un événement, par son organisateur.
 *
 * @param qrContent le contenu brut du QR lu par la caméra (SGILT:C:&lt;code&gt; pour un e-billet de commande)
 */
public record ScanRequest(
        @NotBlank String qrContent
) {}
