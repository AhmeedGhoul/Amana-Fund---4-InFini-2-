package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.*;
import com.ghoul.AmanaFund.security.JwtService;
import com.ghoul.AmanaFund.service.ActivityService;
import com.ghoul.AmanaFund.service.AuthenticationService;
import com.ghoul.AmanaFund.service.FraudCaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("case")
@Tag(name = "Case")
@RequiredArgsConstructor
public class FraudCaseController {

    private final FraudCaseService fraudCaseService;
    private final JwtService jwtService;
    private final AuthenticationService authService;
    private final ActivityService activityService;

    @PostMapping("/CreateCase")
    public ResponseEntity<FraudCases> createCase(@Valid @RequestBody FraudCases fraudCases, @RequestHeader("Authorization") String token) {
        Users createdByUser = extractUser(token);

        fraudCaseService.save(fraudCases,createdByUser);
        logActivity("Case creation", "Case creation succeeded", createdByUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(fraudCases);
    }

    @PutMapping("/ModifyCase")
    public ResponseEntity<FraudCases> modifyCase(@Valid @RequestBody FraudCases fraudCases, @RequestHeader("Authorization") String token) {
        Users modifiedByUser = extractUser(token);
        fraudCaseService.modify(fraudCases);
        logActivity("Case modification", "Case modification succeeded", modifiedByUser);
        return ResponseEntity.status(HttpStatus.OK).body(fraudCases);
    }

    @DeleteMapping("/DeleteCase")
    public ResponseEntity<Void> deleteCase(@RequestBody FraudCases fraudCases, @RequestHeader("Authorization") String token) {
        Users deletedByUser = extractUser(token);
        fraudCaseService.delete(fraudCases);
        logActivity("Case deletion", "Case deletion succeeded", deletedByUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



    private Users extractUser(String token) {
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        return authService.getUserByEmail(email);
    }

    private void logActivity(String action, String description, Users user) {
        activityService.save(new ActivityLog(action, description, LocalDateTime.now(), user, null));
    }
    @GetMapping("/search")
    public ResponseEntity<Page<FraudCases>> searchFraudCases(
            @RequestParam(required = false) String caseType,
            @RequestParam(required = false) LocalDateTime detectionDateTime,
            @RequestParam(required = false) String caseStatus,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer auditId,
            @RequestParam(required = false) List<String> sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<FraudCases> fraudCases = fraudCaseService.searchFraudCases(
                caseType, detectionDateTime, caseStatus, userId, auditId, sortBy, page, size);

        return ResponseEntity.ok(fraudCases);
    }

    @GetMapping("/Case")
    public ResponseEntity<Page<FraudCases>> showFraudCases(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Users requestingUser = extractUser(token);
        logActivity("Cases preview", "Cases preview succeeded", requestingUser);

        Page<FraudCases> fraudCases = fraudCaseService.findAll(page, size);
        return ResponseEntity.ok(fraudCases);
    }

    @GetMapping("/generateFraudCaseReport")
    public ResponseEntity<Void> generateFraudCaseReport(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String directoryPath,
            @RequestParam(required = false) String fileName) throws IOException {
        Users createdByUser = extractUser(token);
        String filePath = fraudCaseService.generateFraudCaseReport(createdByUser, directoryPath, fileName);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
