package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.entity.Audit;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.repository.ActivityLogRepository;
import com.ghoul.AmanaFund.security.JwtService;
import com.ghoul.AmanaFund.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("audit")
@Tag(name = "Audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;
    private final JwtService jwtService;
    private final AuthenticationService authService;
    private final ActivityService activityService;
    private final IpGeolocationService ipGeolocationService;
    private final ActivityLogRepository activityLogRepository;
    private final GroqService groqService;

    @PostMapping("/CreateAudit")
    public ResponseEntity<?> createAudit(@Valid @RequestBody Audit audit, @RequestHeader("Authorization") String token) throws IOException {
        Users createdByUser = extractUser(token);
        String ipAddress = ipGeolocationService.getIpFromIpify();
        String country = ipGeolocationService.getCountryFromGeolocationApi(ipAddress);

        if (audit.getActivityLogs() == null || audit.getActivityLogs().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Cannot create an Audit without at least one Activity Log linked.");
        }

        Audit savedAudit = auditService.save(Audit.builder()
                .dateAudit(audit.getDateAudit())
                .statusAudit(audit.getStatusAudit())
                .output(audit.getOutput())
                .reviewedDate(audit.getReviewedDate())
                .auditType(audit.getAuditType())
                .build()
        );
        for (ActivityLog activityLog : audit.getActivityLogs()) {
            ActivityLog loadedActivity = activityLogRepository.findById(activityLog.getActivityId())
                    .orElseThrow(() -> new IllegalArgumentException("Activity not found: ID " + activityLog.getActivityId()));

            loadedActivity.setAudit(savedAudit);
            activityLogRepository.save(loadedActivity);
        }

        logActivity("Audit creation", "Audit creation succeeded", createdByUser, ipAddress, country);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedAudit);
    }


    @PutMapping("/ModifyAudit")
    public ResponseEntity<Audit> modifyAudit(@Valid @RequestBody Audit audit, @RequestHeader("Authorization") String token) throws IOException {
        Users modifiedByUser = extractUser(token);
        String ipAddress = ipGeolocationService.getIpFromIpify();
        String country = ipGeolocationService.getCountryFromGeolocationApi(ipAddress);
        auditService.modify(audit);
        logActivity("Audit modification", "Audit modification succeeded", modifiedByUser,ipAddress,country);
        return ResponseEntity.status(HttpStatus.OK).body(audit);
    }

    @DeleteMapping("/DeleteAudit/{auditid}")
    public ResponseEntity<Void> deleteAudit(@PathVariable int auditid, @RequestHeader("Authorization") String token) throws IOException {
        Users deletedByUser = extractUser(token);
        String ipAddress = ipGeolocationService.getIpFromIpify();
        String country = ipGeolocationService.getCountryFromGeolocationApi(ipAddress);
        auditService.delete(auditid);
        logActivity("Audit deletion", "Audit deletion succeeded", deletedByUser,ipAddress,country);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/Audit")
    public ResponseEntity<Page<Audit>> getAllAudit(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws IOException {
        Users fetchedByUser = extractUser(token);
        String ipAddress = ipGeolocationService.getIpFromIpify();
        String country = ipGeolocationService.getCountryFromGeolocationApi(ipAddress);
        logActivity("Audit preview", "Audit preview succeeded", fetchedByUser,ipAddress,country);

        Pageable pageable = PageRequest.of(page, size);
        Page<Audit> auditPage = auditService.getAllAudit(pageable);

        return ResponseEntity.ok(auditPage);
    }

    private Users extractUser(String token) {
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        return authService.getUserByEmail(email);
    }

    private void logActivity(String action, String description, Users user,String ipAddress,String country) {
        activityService.save(new ActivityLog(action, description, LocalDateTime.now(), user, null, ipAddress, country));
    }
    @GetMapping("/search")
    public ResponseEntity<Page<Audit>> searchAudits(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String statusAudit,
            @RequestParam(required = false) String output,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String auditType,
            @RequestParam(required = false) List<String> sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Audit> audits = auditService.searchAudits(startDate, statusAudit, output, endDate, auditType, sortBy, page, size);
        return ResponseEntity.ok(audits);
    }


    @GetMapping("/generateAuditReport")
    public ResponseEntity<Void> generateAuditReport(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String directoryPath,
            @RequestParam(required = false) String fileName) throws IOException {
        Users fetchedByUser = extractUser(token);
        String filePath = auditService.generateAuditReport(fetchedByUser, directoryPath, fileName);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("/detect-suspicious-activity/{auditId}")
    public ResponseEntity<String> detectSuspiciousActivity(@PathVariable int auditId) {
        List<ActivityLog> auditLogs = activityLogRepository.findActivityLogByAudit(auditService.getAuditById(auditId));

        if (auditLogs.isEmpty()) {
            return ResponseEntity.ok("No activity logs found for this audit.");
        }

        try {
            String analysis = groqService.analyzeLogs(auditLogs);

            StringBuilder report = new StringBuilder();
            report.append("=== Report for Audit ").append(auditId).append(" at ").append(LocalDateTime.now()).append(" ===\n")
                    .append("Analysis:\n\n")
                    .append(analysis).append("\n\nResult:\n");

            boolean suspicious = analysis.toLowerCase().contains("suspicious") || analysis.toLowerCase().contains("unusual");

            report.append(suspicious ? "Suspicious" : "Normal");
            return ResponseEntity.ok(report.toString());

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error during suspicious activity detection.");
        }
    }
}
