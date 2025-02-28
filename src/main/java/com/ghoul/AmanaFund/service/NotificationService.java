package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.CaseStatus;
import com.ghoul.AmanaFund.entity.FraudCases;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.repository.FraudCaseRepository;
import com.ghoul.AmanaFund.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.ghoul.AmanaFund.entity.EmailTemplateName;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {

    private final FraudCaseRepository fraudCasesRepository;
    private final UserRepository usersRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 45 0 * * *")
    public void notifyUnsolvedCases() throws MessagingException {
        List<Users> allUsers = usersRepository.findAll();

        for (Users user : allUsers) {
            List<FraudCases> unsolvedCases = fraudCasesRepository.findByResponsibleUserAndCaseStatus(user, CaseStatus.PENDING);
            if (!unsolvedCases.isEmpty()) {
                sendNotification(user, unsolvedCases);
            }
        }
    }

    private void sendNotification(Users user, List<FraudCases> fraudCases) throws MessagingException {
        String subject = "Unsolved Fraud Case Updates"; // Email subject
        emailService.sendEmail(user.getEmail(), user.getName(), EmailTemplateName.NEWS_CASES, fraudCases, subject);
    }

}
