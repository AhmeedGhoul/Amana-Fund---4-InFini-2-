package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.repository.ActivityLogRepository;
import com.ghoul.AmanaFund.specification.ActivityLogSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public List<ActivityLog> searchActivityLogs(
            String activityName, String activityDescription, LocalDateTime activityDate,
            Integer userId, Integer auditId, List<String> sortBy) {

        Specification<ActivityLog> spec = ActivityLogSpecification.searchActivityLogs(activityName, activityDescription, activityDate, userId, auditId);

        // Default sort by activityDate DESC
        Sort sort = Sort.by(Sort.Direction.DESC, "activityDate");

        if (sortBy != null && !sortBy.isEmpty()) {
            List<Sort.Order> orders = new ArrayList<>();
            for (String field : sortBy) {
                if (field.startsWith("-")) {
                    orders.add(new Sort.Order(Sort.Direction.DESC, field.substring(1)));
                } else {
                    orders.add(new Sort.Order(Sort.Direction.ASC, field));
                }
            }
            sort = Sort.by(orders);
        }

        return activityLogRepository.findAll(spec, sort);
    }
}
