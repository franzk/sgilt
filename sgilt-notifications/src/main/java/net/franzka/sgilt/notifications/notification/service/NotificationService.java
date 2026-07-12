package net.franzka.sgilt.notifications.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.notifications.notification.domain.Notification;
import net.franzka.sgilt.notifications.notification.domain.NotificationType;
import net.franzka.sgilt.notifications.notification.event.ReservationCreatedEvent;
import net.franzka.sgilt.notifications.notification.exception.NotificationAccessDeniedException;
import net.franzka.sgilt.notifications.notification.exception.NotificationNotFoundException;
import net.franzka.sgilt.notifications.notification.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Gère le cycle de vie des notifications in-app : création à partir des évènements de domaine
 * reçus, consultation et marquage comme lue pour le destinataire authentifié.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private static final int PAGE_SIZE = 10;

    private final NotificationRepository notificationRepository;

    /**
     * Crée la notification "nouvelle demande de réservation" pour le prestataire concerné, à partir
     * des faits bruts publiés par sgilt-core. Construit ici le texte affiché (titre/corps) et le
     * lien de navigation — sgilt-core ne connaît ni l'un ni l'autre.
     *
     * @param event les faits bruts de la réservation créée
     */
    public void createReservationRequestNotification(ReservationCreatedEvent event) {
        String clientDisplayName = event.clientFirstName() + " " + initial(event.clientLastName());
        Notification notification = Notification.builder()
                .recipientEmail(event.recipientEmail())
                .recipientUserId(event.recipientUserId())
                .type(NotificationType.NEW_REQUEST)
                .title("Nouvelle demande")
                .body(clientDisplayName + " vous a envoyé une demande pour " + event.eventTitle() + ".")
                .href("/pro/reservations/" + event.reservationId())
                .read(false)
                .build();

        notificationRepository.save(notification);
        log.info("Notification NEW_REQUEST créée pour {} (réservation {})", event.recipientEmail(), event.reservationId());
    }

    /**
     * Retourne une page des notifications du destinataire, plus récentes d'abord.
     *
     * @param recipientEmail l'email du destinataire authentifié
     * @param page           le numéro de page (0-indexé)
     * @return la page de notifications
     */
    public Page<Notification> getPageForRecipient(String recipientEmail, int page) {
        return notificationRepository.findByRecipientEmailOrderByCreatedAtDesc(recipientEmail, PageRequest.of(page, PAGE_SIZE));
    }

    /**
     * Marque une notification comme lue.
     *
     * @param recipientEmail  l'email du destinataire authentifié
     * @param notificationId  l'identifiant de la notification
     * @throws NotificationNotFoundException     si la notification n'existe pas
     * @throws NotificationAccessDeniedException si la notification n'appartient pas à l'appelant
     */
    public void markRead(String recipientEmail, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));
        if (!notification.getRecipientEmail().equals(recipientEmail)) {
            throw new NotificationAccessDeniedException(notificationId);
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Marque toutes les notifications non lues du destinataire comme lues.
     *
     * @param recipientEmail l'email du destinataire authentifié
     */
    public void markAllRead(String recipientEmail) {
        List<Notification> unread = notificationRepository.findByRecipientEmailAndReadFalse(recipientEmail);
        unread.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unread);
    }

    private String initial(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            return "";
        }
        return lastName.substring(0, 1) + ".";
    }
}
