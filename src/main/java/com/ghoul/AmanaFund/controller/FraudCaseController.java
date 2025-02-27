package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.entity.FraudCases;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.security.JwtService;
import com.ghoul.AmanaFund.service.ActivityService;
import com.ghoul.AmanaFund.service.AuthenticationService;
import com.ghoul.AmanaFund.service.FraudCaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/Case")
    public ResponseEntity<List<FraudCases>> showFraudCases(@RequestHeader("Authorization") String token) {
        Users requestingUser = extractUser(token);
        logActivity("Cases preview", "Cases preview succeeded", requestingUser);
        return ResponseEntity.ok(fraudCaseService.findAll());
    }

    private Users extractUser(String token) {
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        return authService.getUserByEmail(email);
    }

    private void logActivity(String action, String description, Users user) {
        activityService.save(new ActivityLog(action, description, LocalDateTime.now(), user, null));
    }
}
