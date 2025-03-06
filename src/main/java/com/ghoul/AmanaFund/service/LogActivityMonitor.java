package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.repository.ActivityLogRepository;
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


    @Scheduled(cron = "0 00 21 * * *") // Runs at 20:00 (8 PM) every day
    public void detectSuspiciousActivity() {
        LocalDateTime timeLimit = LocalDateTime.now().minusDays(15); // Last 24 hours logs

        List<ActivityLog> recentLogs = activityLogRepository.getRecentLogs(timeLimit);

        try {
            String analysis = groqService.analyzeLogs(recentLogs);
            System.out.println("Groq Analysis: " + analysis);

            // Write analysis to a file
            saveToFile(analysis);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
