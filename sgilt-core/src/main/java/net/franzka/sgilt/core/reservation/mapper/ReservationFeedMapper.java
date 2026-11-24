package net.franzka.sgilt.core.reservation.mapper;

import net.franzka.sgilt.core.reservation.domain.Document;
import net.franzka.sgilt.core.reservation.domain.Note;
import net.franzka.sgilt.core.reservation.domain.ReservationFeed;
import net.franzka.sgilt.core.reservation.dto.FeedFileDto;
import net.franzka.sgilt.core.reservation.dto.FeedItemDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Mapper manuel pour convertir les entités {@link ReservationFeed} en {@link FeedItemDto}.
 * Logique trop conditionnelle pour MapStruct (switch de type, auteur client vs prestataire,
 * regroupement des documents par envoi).
 */
@Component
public class ReservationFeedMapper {

    /**
     * Convertit une liste d'items de feed, en marquant le premier comme message initial. Les
     * documents d'un même envoi ({@code uploadId} commun) forment un seul élément, à la place de
     * son premier document ; un document sans {@code uploadId} est un envoi à lui seul.
     *
     * @param items         la liste d'entités feed, dans l'ordre d'affichage
     * @param reservationId l'identifiant de la réservation (pour construire l'URL des documents)
     * @return la liste des DTOs
     */
    public List<FeedItemDto> toFeedItems(List<? extends ReservationFeed> items, UUID reservationId) {
        // Groupes dans l'ordre de leur premier élément : une note seule, ou les documents d'un envoi.
        Map<UUID, List<ReservationFeed>> groups = new LinkedHashMap<>();
        for (ReservationFeed item : items) {
            groups.computeIfAbsent(groupKey(item), key -> new ArrayList<>()).add(item);
        }

        boolean first = true;
        List<FeedItemDto> result = new ArrayList<>();
        for (List<ReservationFeed> group : groups.values()) {
            result.add(toFeedItem(group, first, reservationId));
            first = false;
        }
        return result;
    }

    /**
     * Convertit un item de feed en DTO. Un document devient un envoi de ce seul document.
     *
     * @param item             l'entité feed (Note ou Document)
     * @param isMessageInitial true si c'est le premier message de la réservation
     * @param reservationId    l'identifiant de la réservation
     * @return le DTO correspondant
     */
    public FeedItemDto toFeedItem(ReservationFeed item, boolean isMessageInitial, UUID reservationId) {
        return toFeedItem(List.of(item), isMessageInitial, reservationId);
    }

    // Une note seule, ou les documents d'un envoi : l'auteur, la date et la visibilité sont ceux du
    // premier élément.
    private FeedItemDto toFeedItem(List<ReservationFeed> group, boolean isMessageInitial, UUID reservationId) {
        ReservationFeed item = group.getFirst();
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
                    .id(groupKey(doc))
                    .authorId(authorId).authorName(authorName).authorPhoto(authorPhoto).authorRole(authorRole)
                    .createdAt(doc.getCreatedAt())
                    .title(doc.getTitle())
                    .isPersonal(doc.getIsPersonal())
                    .isMessageInitial(isMessageInitial)
                    .files(group.stream()
                            .map(Document.class::cast)
                            .map(d -> toFeedFile(d, reservationId))
                            .toList())
                    .build();
            default -> throw new IllegalArgumentException(
                    "Type de feed item inconnu : " + item.getClass().getSimpleName());
        };
    }

    private FeedFileDto toFeedFile(Document doc, UUID reservationId) {
        return new FeedFileDto(
                doc.getId(),
                doc.getFileName(),
                doc.getMimeType(),
                "/reservations/" + reservationId + "/feed/documents/" + doc.getId());
    }

    // Clé de regroupement : l'envoi pour un document qui en a un, l'élément lui-même sinon.
    private UUID groupKey(ReservationFeed item) {
        if (item instanceof Document doc && doc.getUploadId() != null) {
            return doc.getUploadId();
        }
        return item.getId();
    }
}
