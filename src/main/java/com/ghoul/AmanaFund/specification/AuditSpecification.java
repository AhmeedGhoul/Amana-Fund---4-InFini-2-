package com.ghoul.AmanaFund.specification;

import com.ghoul.AmanaFund.entity.Audit;
import com.ghoul.AmanaFund.entity.AuditType;
import com.ghoul.AmanaFund.entity.StatusAudit;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class AuditSpecification {

    public static Specification<Audit> searchAudit(String startDateStr, String statusAudit, String output, String endDateStr, String auditType) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Filter by dateAudit BETWEEN startDate AND endDate
            if (startDateStr != null && !startDateStr.isEmpty()) {
                try {
                    LocalDateTime startDate = LocalDateTime.parse(startDateStr);
                    predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("dateAudit"), startDate));
                } catch (DateTimeParseException ignored) {}
            }

            if (endDateStr != null && !endDateStr.isEmpty()) {
                try {
                    LocalDateTime endDate = LocalDateTime.parse(endDateStr);
                    predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("dateAudit"), endDate));
                } catch (DateTimeParseException ignored) {}
            }

            if (statusAudit != null && !statusAudit.isEmpty()) {
                try {
                    StatusAudit statusEnum = StatusAudit.valueOf(statusAudit.toUpperCase());
                    predicate = cb.and(predicate, cb.equal(root.get("statusAudit"), statusEnum));
                } catch (IllegalArgumentException ignored) {}
            }

            if (output != null && !output.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("output"), "%" + output + "%"));
            }

            if (auditType != null && !auditType.isEmpty()) {
                try {
                    AuditType auditEnum = AuditType.valueOf(auditType.toUpperCase());
                    predicate = cb.and(predicate, cb.equal(root.get("auditType"), auditEnum));
                } catch (IllegalArgumentException ignored) {}
            }

            return predicate;
        };
    }


}
