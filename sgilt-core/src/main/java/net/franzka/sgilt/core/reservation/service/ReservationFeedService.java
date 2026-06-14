package net.franzka.sgilt.core.reservation.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.core.storage.FileStorageException;
import net.franzka.sgilt.core.storage.FileStorageService;
import net.franzka.sgilt.core.storage.FileStreamResult;
import net.franzka.sgilt.core.storage.FileTooLargeException;
import net.franzka.sgilt.core.reservation.domain.*;
import net.franzka.sgilt.core.reservation.dto.AddNoteRequest;
import net.franzka.sgilt.core.reservation.dto.FeedItemDto;
import net.franzka.sgilt.core.reservation.exception.ReservationFeedItemNotFoundException;
import net.franzka.sgilt.core.reservation.exception.ReservationNotAllowedException;
import net.franzka.sgilt.core.reservation.mapper.ReservationFeedMapper;
import net.franzka.sgilt.core.reservation.repository.ReservationFeedRepository;
import net.franzka.sgilt.core.utilisateur.domain.Utilisateur;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service métier pour le feed d'une réservation (notes, documents).
 * L'ownership est vérifié en amont par le controller avant tout appel à ce service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationFeedService {

    private final ReservationService        reservationService;
    private final ReservationFeedRepository feedRepository;
    private final ReservationFeedMapper     feedMapper;
    private final FileStorageService        fileStorageService;

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024; // 10 MB

    /**
     * Retourne le feed d'une réservation filtré selon le rôle de l'appelant.
     * {@link FeedCaller#CLIENT} exclut les éléments personnels du prestataire,
     * {@link FeedCaller#PRESTATAIRE} exclut ceux du client.
     *
     * @param reservationId l'identifiant de la réservation
     * @param caller        le rôle de l'appelant
     * @return les items du feed visibles pour ce rôle
     */
    public List<FeedItemDto> getFeed(UUID reservationId, FeedCaller caller) {
        return feedMapper.toFeedItems(feedRepository.findVisible(reservationId, caller), reservationId);
    }

    /**
     * Ajoute une note à une réservation.
     * Pour {@link FeedCaller#CLIENT}, la note est liée à {@code utilisateur}.
     * Pour {@link FeedCaller#PRESTATAIRE}, elle est liée au prestataire de la réservation.
     *
     * @param reservationId l'identifiant de la réservation
     * @param caller        le rôle de l'appelant
     * @param utilisateur   l'utilisateur client (ignoré si caller est PRESTATAIRE)
     * @param request       le contenu de la note
     * @return la note créée sous forme de {@link FeedItemDto}
     */
    public FeedItemDto addNote(UUID reservationId, FeedCaller caller,
                               @Nullable Utilisateur utilisateur, AddNoteRequest request) {
        Reservation reservation = reservationService.getReservationById(reservationId);
        Note.NoteBuilder builder = Note.builder()
                .reservation(reservation)
                .title(request.title())
                .content(request.content())
                .isPersonal(Boolean.TRUE.equals(request.isPersonal()));
        if (caller == FeedCaller.CLIENT) {
            builder.utilisateur(utilisateur);
        } else {
            builder.prestataire(reservation.getPrestataire());
        }
        return feedMapper.toFeedItem(feedRepository.save(builder.build()), false, reservationId);
    }

    /**
     * Uploade un document vers R2 et l'associe à la réservation.
     * Pour {@link FeedCaller#CLIENT}, le document est lié à {@code utilisateur}.
     * Pour {@link FeedCaller#PRESTATAIRE}, il est lié au prestataire de la réservation.
     *
     * @param reservationId l'identifiant de la réservation
     * @param caller        le rôle de l'appelant
     * @param utilisateur   l'utilisateur client (ignoré si caller est PRESTATAIRE)
     * @param file          le fichier à uploader
     * @param isPersonal    true si le document est personnel
     * @return le document créé sous forme de {@link FeedItemDto}
     */
    public FeedItemDto addDocument(UUID reservationId, FeedCaller caller,
                                   @Nullable Utilisateur utilisateur, MultipartFile file, boolean isPersonal) {
        if (file.getSize() > MAX_FILE_SIZE) throw new FileTooLargeException(MAX_FILE_SIZE);
        Reservation reservation = reservationService.getReservationById(reservationId);
        try {
            String filePath = fileStorageService.uploadDocument(file, "reservation-feed");
            String originalName = file.getOriginalFilename();
            if (originalName == null) {
                log.warn("Upload document sans filename pour la réservation {} — filename absent du Content-Disposition", reservationId);
                originalName = "document";
            }
            Document.DocumentBuilder builder = Document.builder()
                    .reservation(reservation)
                    .title(originalName)
                    .fileName(originalName)
                    .filePath(filePath)
                    .mimeType(file.getContentType())
                    .isPersonal(isPersonal);
            if (caller == FeedCaller.CLIENT) {
                builder.utilisateur(utilisateur);
            } else {
                builder.prestataire(reservation.getPrestataire());
            }
            return feedMapper.toFeedItem(feedRepository.save(builder.build()), false, reservationId);
        } catch (IOException e) {
            throw new FileStorageException("Erreur de stockage du document pour la réservation " + reservationId, e);
        }
    }

    /**
     * Streame un document depuis R2.
     *
     * @param reservationId l'identifiant de la réservation
     * @param documentId    l'identifiant du document
     * @return un {@link FileStreamResult} contenant le flux et les métadonnées
     * @throws IOException en cas d'erreur de lecture R2
     */
    public FileStreamResult streamDocument(UUID reservationId, UUID documentId) throws IOException {
        Document doc = feedRepository.findById(documentId)
                .filter(f -> f.getReservation().getId().equals(reservationId))
                .filter(Document.class::isInstance)
                .map(Document.class::cast)
                .orElseThrow(ReservationFeedItemNotFoundException::new);
        InputStream stream = fileStorageService.streamDocument(doc.getFilePath());
        return new FileStreamResult(stream, doc.getFileName(), doc.getMimeType());
    }

    /**
     * Supprime un document du feed d'une réservation : retire le fichier du bucket
     * et marque l'élément comme supprimé. Seul l'auteur du document peut le supprimer.
     *
     * @param reservationId l'identifiant de la réservation
     * @param documentId    l'identifiant du document
     * @param userId        l'identifiant de l'utilisateur courant
     * @throws ReservationFeedItemNotFoundException si le document n'existe pas dans cette réservation
     * @throws ReservationNotAllowedException        si l'utilisateur courant n'est pas l'auteur du document
     */
    public void deleteDocument(UUID reservationId, UUID documentId, UUID userId) {
        Document doc = feedRepository.findById(documentId)
                .filter(f -> f.getReservation().getId().equals(reservationId))
                .filter(Document.class::isInstance)
                .map(Document.class::cast)
                .orElseThrow(ReservationFeedItemNotFoundException::new);

        UUID authorId = doc.getUtilisateur() != null
                ? doc.getUtilisateur().getId()
                : doc.getPrestataire().getUtilisateur().getId();

        if (!authorId.equals(userId)) {
            throw new ReservationNotAllowedException();
        }

        try {
            fileStorageService.deleteDocument(doc.getFilePath());
        } catch (IOException e) {
            throw new FileStorageException("Erreur de suppression du document " + documentId, e);
        }
        doc.setDeletedAt(LocalDateTime.now());
        feedRepository.save(doc);
    }

}
