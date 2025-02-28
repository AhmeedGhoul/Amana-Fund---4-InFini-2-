package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityLogRepository activityLogRepository;
    public void save(ActivityLog activityLog) {
        activityLogRepository.save(activityLog);
    }
    public List<ActivityLog> findAll() {
       return activityLogRepository.findAll();
    }

}
