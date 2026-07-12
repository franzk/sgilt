package net.franzka.sgilt.notifications.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.franzka.sgilt.notifications.notification.api.NotificationApi;
import net.franzka.sgilt.notifications.notification.domain.Notification;
import net.franzka.sgilt.notifications.notification.dto.NotificationPageDto;
import net.franzka.sgilt.notifications.notification.mapper.NotificationMapper;
import net.franzka.sgilt.notifications.notification.service.CurrentUserService;
import net.franzka.sgilt.notifications.notification.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController implements NotificationApi {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<NotificationPageDto> getPage(int page) {
        String email = currentUserService.getEmail();
        log.info("GET /api/v1/notifications?page={}", page);
        Page<Notification> result = notificationService.getPageForRecipient(email, page);
        NotificationPageDto dto = new NotificationPageDto(
                result.getContent().stream().map(notificationMapper::toDto).toList(),
                result.hasNext());
        return ResponseEntity.ok(dto);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> markRead(UUID notificationId) {
        String email = currentUserService.getEmail();
        log.info("PATCH /api/v1/notifications/{}/read", notificationId);
        notificationService.markRead(email, notificationId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> markAllRead() {
        String email = currentUserService.getEmail();
        log.info("PATCH /api/v1/notifications/read");
        notificationService.markAllRead(email);
        return ResponseEntity.noContent().build();
    }
}
