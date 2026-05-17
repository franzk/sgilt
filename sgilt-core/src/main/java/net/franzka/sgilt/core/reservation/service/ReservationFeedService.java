package net.franzka.sgilt.core.reservation.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.reservation.domain.Document;
import net.franzka.sgilt.core.reservation.domain.FeedCaller;
import net.franzka.sgilt.core.reservation.domain.Note;
import net.franzka.sgilt.core.reservation.domain.Reservation;
import net.franzka.sgilt.core.reservation.domain.ReservationFeed;
import net.franzka.sgilt.core.reservation.dto.AddNoteRequest;
import net.franzka.sgilt.core.reservation.dto.FeedItemDto;
import net.franzka.sgilt.core.reservation.repository.NoteRepository;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service métier pour le feed d'une réservation (notes, documents).
 */
@Service
@RequiredArgsConstructor
public class ReservationFeedService {

    private final ReservationService reservationService;
    private final NoteRepository noteRepository;

    /**
     * Retourne le feed visible par le client pour une réservation.
     * Les notes personnelles du prestataire sont exclues au niveau de la requête.
     *
     * @param reservationId l'identifiant de la réservation
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @return le feed de la réservation
     */
    public List<FeedItemDto> getFeedForClient(UUID reservationId, UUID utilisateurId) {
        reservationService.getReservation(reservationId, utilisateurId);
        return toFeedItems(noteRepository.findVisible(reservationId, FeedCaller.CLIENT));
    }

    /**
     * Retourne le feed visible par le prestataire pour une réservation.
     * Les notes personnelles du client sont exclues au niveau de la requête.
     *
     * @param reservationId l'identifiant de la réservation
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @return le feed de la réservation
     */
    public List<FeedItemDto> getFeedForPrestataire(UUID reservationId, UUID utilisateurId) {
        reservationService.getReservation(reservationId, utilisateurId);
        return toFeedItems(noteRepository.findVisible(reservationId, FeedCaller.PRESTATAIRE));
    }

    /**
     * Ajoute une note à une réservation.
     *
     * @param reservationId l'identifiant de la réservation
     * @param utilisateur   l'utilisateur connecté (auteur de la note)
     * @param request       le contenu de la note
     * @return la note créée sous forme de {@link FeedItemDto}
     */
    public FeedItemDto addNote(UUID reservationId, Utilisateur utilisateur, AddNoteRequest request) {
        Reservation reservation = reservationService.getReservation(reservationId, utilisateur.getId());
        Note note = Note.builder()
                .reservation(reservation)
                .utilisateur(utilisateur)
                .title(request.title())
                .content(request.content())
                .isPersonal(request.personal())
                .build();
        note = noteRepository.save(note);
        return toFeedItem(note, false);
    }

    private List<FeedItemDto> toFeedItems(List<? extends ReservationFeed> items) {
        boolean first = true;
        List<FeedItemDto> result = new ArrayList<>();
        for (ReservationFeed item : items) {
            result.add(toFeedItem(item, first));
            first = false;
        }
        return result;
    }

    private FeedItemDto toFeedItem(ReservationFeed item, boolean isMessageInitial) {
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
                    doc.getTitle(), null, null, isMessageInitial,
                    doc.getFileName(), doc.getMimeType(), doc.getUrl()
            );
            default -> throw new IllegalArgumentException(
                    "Type de feed item inconnu : " + item.getClass().getSimpleName());
        };
    }
}
