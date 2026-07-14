package net.franzka.sgilt.notifications.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.notifications.notification.domain.Notification;
import net.franzka.sgilt.notifications.notification.event.ReservationCreatedEvent;
import net.franzka.sgilt.notifications.notification.event.ReservationFeedItemAddedEvent;
import net.franzka.sgilt.notifications.notification.event.ReservationStatusChangedEvent;
import net.franzka.sgilt.notifications.notification.exception.NotificationAccessDeniedException;
import net.franzka.sgilt.notifications.notification.exception.NotificationNotFoundException;
import net.franzka.sgilt.notifications.notification.mapper.NotificationEventMapper;
import net.franzka.sgilt.notifications.notification.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Gère le cycle de vie des notifications in-app : création à partir des évènements de domaine
 * reçus, consultation et marquage comme lue pour le destinataire authentifié. La construction de la
 * notification à partir de l'évènement (clé i18n, params, lien) est déléguée à
 * {@link NotificationEventMapper} — un nouveau type d'évènement ajoute une méthode là-bas et un cas
 * dans le {@code switch} de {@link #createFromEvent}, pas de nouvelle méthode ici.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private static final int PAGE_SIZE = 10;

    private final NotificationRepository notificationRepository;
    private final NotificationEventMapper notificationEventMapper;

    /**
     * Crée et persiste la notification correspondant à l'évènement de domaine reçu, quel que soit son
     * type — {@code DomainEventListener} n'a besoin de connaître que la classe cible pour la
     * désérialisation, pas la méthode de création à appeler.
     *
     * @param event l'évènement de domaine désérialisé (ex. {@link ReservationCreatedEvent})
     * @throws IllegalArgumentException si le type de l'évènement n'est pas géré
     */
    public void createFromEvent(Object event) {
        Notification notification = switch (event) {
            case ReservationCreatedEvent e -> notificationEventMapper.toNotification(e);
            case ReservationStatusChangedEvent e -> notificationEventMapper.toNotification(e);
            case ReservationFeedItemAddedEvent e -> notificationEventMapper.toNotification(e);
            default -> throw new IllegalArgumentException("Type d'évènement de notification inconnu : " + event.getClass());
        };

        notificationRepository.save(notification);
        log.info("Notification {} créée pour {} ({})", notification.getType(), notification.getRecipientEmail(), notification.getHref());
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
}
