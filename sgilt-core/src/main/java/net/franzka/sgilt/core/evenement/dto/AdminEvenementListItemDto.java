package net.franzka.sgilt.core.evenement.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO allégé d'un événement pour la liste du back-office admin (choix de l'événement dont on
 * saisit la page publique).
 *
 * @param id         l'identifiant de l'événement
 * @param title      le titre de l'événement
 * @param date       la date de l'événement
 * @param ownerEmail l'e-mail de son propriétaire
 * @param slug       le slug de sa page publique, ou null s'il n'en a pas
 */
public record AdminEvenementListItemDto(
        UUID id,
        String title,
        LocalDate date,
        String ownerEmail,
        String slug
) {}
