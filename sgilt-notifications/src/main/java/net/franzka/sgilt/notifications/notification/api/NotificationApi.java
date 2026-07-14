package net.franzka.sgilt.notifications.notification.api;

import net.franzka.sgilt.notifications.notification.dto.NotificationPageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@RequestMapping("api/v1/notifications")
public interface NotificationApi {

    @GetMapping
    ResponseEntity<NotificationPageDto> getPage(@RequestParam(defaultValue = "0") int page);

    @PatchMapping("/{notificationId}/read")
    ResponseEntity<Void> markRead(@PathVariable UUID notificationId);

    @PatchMapping("/read")
    ResponseEntity<Void> markAllRead();
}
