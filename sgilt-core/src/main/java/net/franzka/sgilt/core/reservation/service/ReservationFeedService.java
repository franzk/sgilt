package net.franzka.sgilt.core.reservation.service;

import lombok.RequiredArgsConstructor;
import net.franzka.sgilt.core.storage.FileStorageException;
import net.franzka.sgilt.core.storage.FileStorageService;
import net.franzka.sgilt.core.storage.FileStreamResult;
import net.franzka.sgilt.core.reservation.domain.*;
import net.franzka.sgilt.core.reservation.dto.AddNoteRequest;
import net.franzka.sgilt.core.reservation.dto.FeedItemDto;
import net.franzka.sgilt.core.reservation.exception.ReservationFeedItemNotFoundException;
import net.franzka.sgilt.core.reservation.repository.ReservationFeedRepository;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service métier pour le feed d'une réservation (notes, documents).
 */
@Service
@RequiredArgsConstructor
public class ReservationFeedService {

    private final ReservationService        reservationService;
    private final ReservationFeedRepository feedRepository;
    private final FileStorageService        fileStorageService;

    /**
     * Retourne le feed visible par le client pour une réservation.
     * Les éléments personnels du prestataire sont exclus en base.
     */
    public List<FeedItemDto> getFeedForClient(UUID reservationId, UUID utilisateurId) {
        reservationService.getReservation(reservationId, utilisateurId);
        return toFeedItems(feedRepository.findVisible(reservationId, FeedCaller.CLIENT), reservationId);
    }

    /**
     * Retourne le feed visible par le prestataire pour une réservation.
     * Les éléments personnels du client sont exclus en base.
     */
    public List<FeedItemDto> getFeedForPrestataire(UUID reservationId, UUID utilisateurId) {
        reservationService.getReservation(reservationId, utilisateurId);
        return toFeedItems(feedRepository.findVisible(reservationId, FeedCaller.PRESTATAIRE), reservationId);
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
                .isPersonal(Boolean.TRUE.equals(request.isPersonal()))
                .build();
        note = feedRepository.save(note);
        return toFeedItem(note, false, reservationId);
    }

    /**
     * Uploade un document vers R2 et l'associe à la réservation.
     *
     * @param reservationId l'identifiant de la réservation
     * @param utilisateur   l'utilisateur connecté (auteur du document)
     * @param file          le fichier à uploader
     * @param isPersonal    true si le document est personnel
     * @return le document créé sous forme de {@link FeedItemDto}
     */
    public FeedItemDto addDocument(UUID reservationId, Utilisateur utilisateur, MultipartFile file, boolean isPersonal) {
        Reservation reservation = reservationService.getReservation(reservationId, utilisateur.getId());
        try {
            String filePath = fileStorageService.upload(file, "documents");
            String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
            Document doc = Document.builder()
                    .reservation(reservation)
                    .utilisateur(utilisateur)
                    .title(originalName)
                    .fileName(originalName)
                    .filePath(filePath)
                    .mimeType(file.getContentType())
                    .isPersonal(isPersonal)
                    .build();
            doc = feedRepository.save(doc);
            return toFeedItem(doc, false, reservationId);
        } catch (IOException e) {
            throw new FileStorageException("Erreur de stockage du document pour la réservation " + reservationId, e);
        }
    }

    /**
     * Streame un document depuis R2 après vérification de l'accès.
     *
     * @param reservationId l'identifiant de la réservation
     * @param documentId    l'identifiant du document
     * @param utilisateurId l'identifiant de l'utilisateur connecté
     * @return un {@link FileStreamResult} contenant le flux et les métadonnées
     * @throws IOException en cas d'erreur de lecture R2
     */
    public FileStreamResult streamDocument(UUID reservationId, UUID documentId, UUID utilisateurId) throws IOException {
        reservationService.getReservation(reservationId, utilisateurId);
        Document doc = feedRepository.findById(documentId)
                .filter(f -> f.getReservation().getId().equals(reservationId))
                .filter(Document.class::isInstance)
                .map(Document.class::cast)
                .orElseThrow(ReservationFeedItemNotFoundException::new);
        String filePath = doc.getFilePath();
        String fileName = doc.getFileName();
        String mimeType = doc.getMimeType();
        InputStream stream = fileStorageService.stream(filePath);
        return new FileStreamResult(stream, fileName, mimeType);
    }

    private List<FeedItemDto> toFeedItems(List<? extends ReservationFeed> items, UUID reservationId) {
        boolean first = true;
        List<FeedItemDto> result = new ArrayList<>();
        for (ReservationFeed item : items) {
            result.add(toFeedItem(item, first, reservationId));
            first = false;
        }
        return result;
    }

    private FeedItemDto toFeedItem(ReservationFeed item, boolean isMessageInitial, UUID reservationId) {
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
