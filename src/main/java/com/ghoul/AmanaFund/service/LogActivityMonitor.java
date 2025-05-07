package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.repository.ActivityLogRepository;
import com.ghoul.AmanaFund.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LogActivityMonitor {
    private final GroqService groqService;
    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;


    public void detectSuspiciousActivity() {
        LocalDateTime timeLimit = LocalDateTime.now().minusDays(15);

        List<ActivityLog> recentLogs = activityLogRepository.getRecentLogs(timeLimit);

        if (recentLogs.isEmpty()) {
            System.out.println("No recent activity logs found.");
            return;
        }

        try {
            String analysis = groqService.analyzeLogs(recentLogs);
            System.out.println("Groq Analysis: " + analysis);

            saveToFile(analysis);
            for (ActivityLog log : recentLogs) {
                Users user = log.getUser();

                if (user != null) {
                    int scoreChange = evaluateSuspicion(analysis, log);
                    user.setUserScore(user.getUserScore() + scoreChange);

                    userRepository.save(user);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int evaluateSuspicion(String analysis, ActivityLog log) {
        String userActivity = log.getActivityName().toLowerCase();

        if (analysis.contains("suspicious") && analysis.contains(log.getUser().getUsername())) {
            return -10;
        } else if (analysis.contains("anomaly") || userActivity.contains("failed login")) {
            return -5;
        } else if (analysis.contains("normal activity") || userActivity.contains("successful login")) {
            return +2;
        }

        return 0;
    }


    private void saveToFile(String content) throws IOException {
        // Get the desktop directory
        String desktopPath = System.getProperty("user.home") + "/Desktop";
        String fileName = desktopPath + "/suspicious_activity_report.txt";

        // Format timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = "=== Report Generated at " + timestamp + " ===\n" + content + "\n\n";

        // Write to file (append if exists, create if not)
        Path path = Path.of(fileName);
        Files.write(path, logEntry.getBytes(),
                Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);

        System.out.println("Suspicious activity report saved to: " + fileName);

        // Clean up old logs
        deleteOldReports(desktopPath);
    }

    private void deleteOldReports(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> name.startsWith("suspicious_activity_report"));

        if (files != null) {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(15);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (File file : files) {
                try {
                    String firstLine = Files.readAllLines(file.toPath()).get(0);
                    String dateString = firstLine.replace("=== Report Generated at ", "").replace(" ===", "").trim();
                    LocalDateTime fileDate = LocalDateTime.parse(dateString, formatter);

                    if (fileDate.isBefore(cutoffDate)) {
                        file.delete();
                        System.out.println("Deleted old report: " + file.getName());
                    }
                } catch (Exception ignored) {
                    // Ignore files that don't follow the expected format
                }
            }
        }
    }

}
