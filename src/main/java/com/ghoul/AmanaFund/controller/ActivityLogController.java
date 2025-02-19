package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.security.JwtService;
import com.ghoul.AmanaFund.service.ActivityService;
import com.ghoul.AmanaFund.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
@RestController
@RequestMapping("ActivityLog")
@Tag(name = "ActivityLog")
@RequiredArgsConstructor
public class ActivityLogController {
    private final ActivityService activityService;
    private final JwtService jwtService;
    private final AuthenticationService service;
    @GetMapping("/ActivityLog")
    public ResponseEntity<List<ActivityLog>> showActivityLog(@RequestHeader("Authorization") String token) throws MessagingException {
        List<ActivityLog> activityLog = activityService.findAll();
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        Users deletedByUser = service.getUserByEmail(email);
        activityService.save(new ActivityLog("Activity Preview","Activity Preview succeeded" , LocalDateTime.now(),deletedByUser,null));

        return ResponseEntity.ok(activityLog);
    }

}
