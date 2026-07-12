package net.franzka.sgilt.notifications.notification.repository;

import net.franzka.sgilt.notifications.notification.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByRecipientEmailOrderByCreatedAtDesc(String recipientEmail, Pageable pageable);

    List<Notification> findByRecipientEmailAndReadFalse(String recipientEmail);
}
