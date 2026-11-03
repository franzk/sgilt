package net.franzka.sgilt.core.evenement.billetterie.commande.dto;

import net.franzka.sgilt.core.evenement.page.dto.PracticalInfoDto;

/**
 * E-billet d'une commande payée, pour sa page publique : l'événement, ses infos pratiques, le
 * nombre de places et le QR de la commande (un seul, qui fait entrer toutes ses places).
 *
 * @param title         le titre de l'événement
 * @param heroImage     le chemin de l'image de l'événement, ou null
 * @param eventType     le type de l'événement, ou null (le front en tire l'image par défaut)
 * @param practicalInfo les infos pratiques de l'événement
 * @param quantity      le nombre de places de la commande
 * @param qrContent     le contenu du QR de la commande ({@code SGILT:C:<code>})
 */
public record EBilletDto(
        String title,
        String heroImage,
        String eventType,
        PracticalInfoDto practicalInfo,
        int quantity,
        String qrContent
) {}
