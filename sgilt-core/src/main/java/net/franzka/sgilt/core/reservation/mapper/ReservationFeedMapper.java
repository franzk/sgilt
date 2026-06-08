package net.franzka.sgilt.core.reservation.mapper;

import net.franzka.sgilt.core.reservation.domain.Document;
import net.franzka.sgilt.core.reservation.domain.Note;
import net.franzka.sgilt.core.reservation.domain.ReservationFeed;
import net.franzka.sgilt.core.reservation.dto.FeedItemDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Mapper manuel pour convertir les entités {@link ReservationFeed} en {@link FeedItemDto}.
 * Logique trop conditionnelle pour MapStruct (switch de type, auteur client vs prestataire).
 */
@Component
public class ReservationFeedMapper {

    /**
     * Convertit une liste d'items de feed, en marquant le premier comme message initial.
     *
     * @param items         la liste d'entités feed
     * @param reservationId l'identifiant de la réservation (pour construire l'URL des documents)
     * @return la liste des DTOs
     */
    public List<FeedItemDto> toFeedItems(List<? extends ReservationFeed> items, UUID reservationId) {
        boolean first = true;
        List<FeedItemDto> result = new ArrayList<>();
        for (ReservationFeed item : items) {
            result.add(toFeedItem(item, first, reservationId));
            first = false;
        }
        return result;
    }

    /**
     * Convertit un item de feed en DTO.
     *
     * @param item             l'entité feed (Note ou Document)
     * @param isMessageInitial true si c'est le premier message de la réservation
     * @param reservationId    l'identifiant de la réservation
     * @return le DTO correspondant
     */
    public FeedItemDto toFeedItem(ReservationFeed item, boolean isMessageInitial, UUID reservationId) {
        boolean byClient   = item.getUtilisateur() != null;
        String authorId    = byClient ? item.getUtilisateur().getId().toString()
                                      : item.getPrestataire().getUtilisateur().getId().toString();
        String authorName  = byClient ? item.getUtilisateur().getFirstName() + " " + item.getUtilisateur().getLastName()
                                      : item.getPrestataire().getName();
        String authorPhoto = byClient ? item.getUtilisateur().getAvatarUrl()
                                      : item.getPrestataire().getAvatar();
        String authorRole  = byClient ? "client" : "prestataire";

        return switch (item) {
            case Note note -> new FeedItemDto(
                    "note",
                    note.getId(), authorId, authorName, authorPhoto, authorRole, note.getCreatedAt(),
                    note.getTitle(), note.getContent(), note.getIsPersonal(), isMessageInitial,
                    null, null, null
            );
            case Document doc -> new FeedItemDto(
                    "document",
                    doc.getId(), authorId, authorName, authorPhoto, authorRole, doc.getCreatedAt(),
                    doc.getTitle(), null, doc.getIsPersonal(), isMessageInitial,
                    doc.getFileName(), doc.getMimeType(),
                    "/reservations/" + reservationId + "/feed/documents/" + doc.getId()
            );
            default -> throw new IllegalArgumentException(
                    "Type de feed item inconnu : " + item.getClass().getSimpleName());
        };
    }
}
