package com.ghoul.AmanaFund.specification;

import com.ghoul.AmanaFund.entity.Audit;
import com.ghoul.AmanaFund.entity.AuditType;
import com.ghoul.AmanaFund.entity.StatusAudit;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
public class AuditSpecification {

public static Specification<Audit> searchAudit(LocalDateTime dateAudit, String statusAudit, String output, LocalDateTime reviewedDate, String auditType) {
    return (root, query, criteriaBuilder) -> {
        Predicate predicate = criteriaBuilder.conjunction();

        if (dateAudit != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("dateAudit"), dateAudit));
        }
        if (statusAudit != null) {
            StatusAudit statusEnum = StatusAudit.valueOf(statusAudit.toUpperCase());
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("statusAudit"), statusEnum));
        }
        if (output != null && !output.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("output"), "%" + output + "%"));
        }
        if (reviewedDate != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("reviewedDate"), reviewedDate));
        }

        if (auditType != null) {
            AuditType auditEnum = AuditType.valueOf(auditType.toUpperCase());
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("auditType"), auditEnum));
        }

        return predicate;
    };
}
}