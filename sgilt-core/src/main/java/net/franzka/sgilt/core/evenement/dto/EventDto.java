package net.franzka.sgilt.core.evenement.dto;

import java.util.List;

/**
 * Tout un événement pour l'EventBoard : ses métadonnées et ses rubriques, chacune avec ses
 * réservations.
 *
 * @param meta      les métadonnées de l'événement
 * @param rubriques les rubriques, dans l'ordre d'affichage
 */
public record EventDto(
        EventMetaDto meta,
        List<EventRubriqueDto> rubriques
) {}
