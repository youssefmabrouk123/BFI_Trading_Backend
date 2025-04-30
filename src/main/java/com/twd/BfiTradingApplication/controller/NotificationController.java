package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.dto.NotificationDTO;
import com.twd.BfiTradingApplication.service.PendingOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired
    private PendingOrderService pendingOrderService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable Integer userId) {
        List<NotificationDTO> notifications = pendingOrderService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/today/{userId}")
    public ResponseEntity<List<NotificationDTO>> getAllNotificationsForToday(@PathVariable Integer userId) {
        List<NotificationDTO> notifications = pendingOrderService.getAllNotificationsForToday(userId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/mark-read/{notificationId}")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable Integer notificationId,
            @RequestBody Map<String, Integer> body) {
        Integer userId = body.get("userId");
        if (userId == null) return ResponseEntity.badRequest().build();
        pendingOrderService.markNotificationAsRead(notificationId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<Void> markAllNotificationsAsRead(@RequestBody Map<String, Integer> body) {
        Integer userId = body.get("userId");
        if (userId == null) return ResponseEntity.badRequest().build();
        pendingOrderService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Integer notificationId,
            @RequestBody Map<String, Integer> body) {
        Integer userId = body.get("userId");
        if (userId == null) return ResponseEntity.badRequest().build();
        pendingOrderService.deleteNotification(notificationId, userId);
        return ResponseEntity.noContent().build();
    }
}