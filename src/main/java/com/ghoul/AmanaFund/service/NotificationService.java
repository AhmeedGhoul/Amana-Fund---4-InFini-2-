package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.*;
import com.ghoul.AmanaFund.repository.FraudCaseRepository;
import com.ghoul.AmanaFund.repository.NotificationRepository;
import com.ghoul.AmanaFund.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final FraudCaseRepository fraudCaseRepository;

    public void notifyUser(Users user, String message) {
        Notification notification = new Notification(null, message, false, user, LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public List<Notification> getUnseenNotifications(Users user) {
        return notificationRepository.findByUserAndSeenFalse(user);
    }

    public void markAsSeen(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setSeen(true);
            notificationRepository.save(n);
        });
    }
    public void notifyPendingFraudCases(Users user) {
        List<FraudCases> pendingCases = fraudCaseRepository.findByResponsibleUserAndCaseStatus(user, CaseStatus.PENDING);
        if (!pendingCases.isEmpty()) {
            String message = "You have pending fraud cases to review!";
            notifyUser(user, message);  // Notify the user
        }
    }
}

