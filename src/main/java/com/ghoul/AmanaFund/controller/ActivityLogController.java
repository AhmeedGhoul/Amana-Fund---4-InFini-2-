package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.security.JwtService;
import com.ghoul.AmanaFund.service.ActivityService;
import com.ghoul.AmanaFund.service.AuthenticationService;
import com.ghoul.AmanaFund.service.IpGeolocationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
@RestController
@RequestMapping("ActivityLog")
@Tag(name = "ActivityLog")
@RequiredArgsConstructor
public class ActivityLogController {
    private final ActivityService activityService;
    private final JwtService jwtService;
    private final IpGeolocationService ipGeolocationService;
    private final AuthenticationService service;
    @GetMapping("/ActivityLog")
    public ResponseEntity<Page<ActivityLog>> showActivityLog(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws IOException {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLog> activityLog = activityService.findAll(pageable);
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        Users deletedByUser = service.getUserByEmail(email);
        String ipAddress = ipGeolocationService.getIpFromIpify();
        String country = ipGeolocationService.getCountryFromGeolocationApi(ipAddress);
        activityService.save(new ActivityLog("Activity Preview", "Activity Preview succeeded", LocalDateTime.now(), deletedByUser, null,ipAddress,country));
        return ResponseEntity.ok(activityLog);
    }
    @GetMapping("/search")
    public ResponseEntity<Page<ActivityLog>> searchActivityLogs(
            @RequestParam(required = false) String activityName,
            @RequestParam(required = false) String activityDescription,
            @RequestParam(required = false) LocalDateTime activityDate,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer auditId,
            @RequestParam(required = false) List<String> sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ActivityLog> activityLogs = activityService.searchActivityLogs(activityName, activityDescription, activityDate, userId, auditId, sortBy, page, size);
        return ResponseEntity.ok(activityLogs);
    }
    @GetMapping("/generateActivityLogReport")
    public ResponseEntity<Void> generateActivityLogReport(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String directoryPath,
            @RequestParam(required = false) String fileName) throws IOException {
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        Users deletedByUser = service.getUserByEmail(email);
        String filePath = activityService.generateActivityLogReport(deletedByUser, directoryPath, fileName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
