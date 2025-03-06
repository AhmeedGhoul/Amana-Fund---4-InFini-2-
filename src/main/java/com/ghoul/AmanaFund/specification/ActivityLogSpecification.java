package com.ghoul.AmanaFund.specification;

import com.ghoul.AmanaFund.entity.ActivityLog;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public class ActivityLogSpecification {

    public static Specification<ActivityLog> searchActivityLogs(
            String activityName, String activityDescription, LocalDateTime activityDate, Integer userId, Integer auditId) {

        return (root, query, criteriaBuilder) -> {
            Specification<ActivityLog> spec = Specification.where(null);

            if (activityName != null && !activityName.isEmpty()) {
                spec = spec.and((root1, query1, cb) -> cb.like(cb.lower(root1.get("activityName")), "%" + activityName.toLowerCase() + "%"));
            }
            if (activityDescription != null && !activityDescription.isEmpty()) {
                spec = spec.and((root1, query1, cb) -> cb.like(cb.lower(root1.get("activityDescription")), "%" + activityDescription.toLowerCase() + "%"));
            }
            if (activityDate != null) {
                spec = spec.and((root1, query1, cb) -> cb.equal(root1.get("activityDate"), activityDate));
            }
            if (userId != null) {
                spec = spec.and((root1, query1, cb) -> cb.equal(root1.get("user").get("id"), userId));
            }
            if (auditId != null) {
                spec = spec.and((root1, query1, cb) -> cb.equal(root1.get("audit").get("auditId"), auditId));
            }

            return spec.toPredicate(root, query, criteriaBuilder);
        };
    }
}
