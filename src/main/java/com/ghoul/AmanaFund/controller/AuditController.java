package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.entity.Audit;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.security.JwtService;
import com.ghoul.AmanaFund.service.ActivityService;
import com.ghoul.AmanaFund.service.AuditService;
import com.ghoul.AmanaFund.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/CreateAudit")
    public ResponseEntity<Audit> createAudit(@Valid @RequestBody Audit audit, @RequestHeader("Authorization") String token) {
        Users createdByUser = extractUser(token);
        auditService.save(audit);
        logActivity("Audit creation", "Audit creation succeeded", createdByUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(audit);
    }

    @PutMapping("/ModifyAudit")
    public ResponseEntity<Audit> modifyAudit(@Valid @RequestBody Audit audit, @RequestHeader("Authorization") String token) {
        Users modifiedByUser = extractUser(token);
        auditService.modify(audit);
        logActivity("Audit modification", "Audit modification succeeded", modifiedByUser);
        return ResponseEntity.status(HttpStatus.OK).body(audit);
    }

    @DeleteMapping("/DeleteAudit")
    public ResponseEntity<Void> deleteAudit(@RequestBody Audit audit, @RequestHeader("Authorization") String token) {
        Users deletedByUser = extractUser(token);
        auditService.delete(audit);
        logActivity("Audit deletion", "Audit deletion succeeded", deletedByUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/Audit")
    public ResponseEntity<List<Audit>> getAllAudit(@RequestHeader("Authorization") String token) {
        Users fetchedByUser = extractUser(token);
        logActivity("Audit preview", "Audit preview succeeded", fetchedByUser);
        return ResponseEntity.ok(auditService.getAllAudit());
    }

    private Users extractUser(String token) {
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        return authService.getUserByEmail(email);
    }

    private void logActivity(String action, String description, Users user) {
        activityService.save(new ActivityLog(action, description, LocalDateTime.now(), user, null));
    }
}
