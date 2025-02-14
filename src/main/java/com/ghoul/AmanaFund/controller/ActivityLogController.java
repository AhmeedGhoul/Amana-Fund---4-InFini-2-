package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.service.ActivityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("ActivityLog")
@Tag(name = "ActivityLog")
@RequiredArgsConstructor
public class ActivityLogController {
    private final ActivityService activityService;
    @GetMapping("/ActivityLog")
    public ResponseEntity<List<ActivityLog>> showActivityLog() throws MessagingException {
        List<ActivityLog> activityLog = activityService.findAll();
        return ResponseEntity.ok(activityLog);
    }

}
