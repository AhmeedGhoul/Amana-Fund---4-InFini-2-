package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.Notification;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.repository.UserRepository;
import com.ghoul.AmanaFund.security.JwtService;
import com.ghoul.AmanaFund.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @GetMapping("/unseen")
    public ResponseEntity<List<Notification>> getUnseenNotifications(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractUsername(token);
        Users user = userRepository.findByEmail(email).orElseThrow();

        List<Notification> notifications = notificationService.getUnseenNotifications(user);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/seen/{id}")
    public ResponseEntity<?> markAsSeen(@PathVariable Long id) {
        notificationService.markAsSeen(id);
        return ResponseEntity.ok().build();
    }
}
