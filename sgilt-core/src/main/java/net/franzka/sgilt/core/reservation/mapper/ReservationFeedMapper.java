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
            case Note note -> FeedItemDto.builder()
                    .type("note")
                    .id(note.getId())
                    .authorId(authorId).authorName(authorName).authorPhoto(authorPhoto).authorRole(authorRole)
                    .createdAt(note.getCreatedAt())
                    .title(note.getTitle())
                    .content(note.getContent())
                    .isPersonal(note.getIsPersonal())
                    .isMessageInitial(isMessageInitial)
                    .generatedKey(note.getGeneratedKey())
                    .build();
            case Document doc -> FeedItemDto.builder()
                    .type("document")
                    .id(doc.getId())
                    .authorId(authorId).authorName(authorName).authorPhoto(authorPhoto).authorRole(authorRole)
                    .createdAt(doc.getCreatedAt())
                    .title(doc.getTitle())
                    .isPersonal(doc.getIsPersonal())
                    .isMessageInitial(isMessageInitial)
                    .name(doc.getFileName())
                    .fileType(doc.getMimeType())
                    .url("/reservations/" + reservationId + "/feed/documents/" + doc.getId())
                    .build();
            default -> throw new IllegalArgumentException(
                    "Type de feed item inconnu : " + item.getClass().getSimpleName());
        };
    }
}
