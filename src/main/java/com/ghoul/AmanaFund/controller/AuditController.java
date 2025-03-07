package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.entity.Audit;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.security.JwtService;
import com.ghoul.AmanaFund.service.ActivityService;
import com.ghoul.AmanaFund.service.AuditService;
import com.ghoul.AmanaFund.service.AuthenticationService;
import com.ghoul.AmanaFund.service.IpGeolocationService;
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

    @PostMapping("/CreateAudit")
    public ResponseEntity<Audit> createAudit(@Valid @RequestBody Audit audit, @RequestHeader("Authorization") String token) throws IOException {
        Users createdByUser = extractUser(token);
        String ipAddress = ipGeolocationService.getIpFromIpify();
        String country = ipGeolocationService.getCountryFromGeolocationApi(ipAddress);
        auditService.save(audit);
        logActivity("Audit creation", "Audit creation succeeded", createdByUser,ipAddress,country);
        return ResponseEntity.status(HttpStatus.CREATED).body(audit);
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

    @DeleteMapping("/DeleteAudit")
    public ResponseEntity<Void> deleteAudit(@RequestBody Audit audit, @RequestHeader("Authorization") String token) throws IOException {
        Users deletedByUser = extractUser(token);
        String ipAddress = ipGeolocationService.getIpFromIpify();
        String country = ipGeolocationService.getCountryFromGeolocationApi(ipAddress);
        auditService.delete(audit);
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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateAudit,
            @RequestParam(required = false) String statusAudit,
            @RequestParam(required = false) String output,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime reviewedDate,
            @RequestParam(required = false) String auditType,
            @RequestParam(required = false) List<String> sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Audit> audits = auditService.searchAudits(dateAudit, statusAudit, output, reviewedDate, auditType, sortBy, page, size);
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
}
