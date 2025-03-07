package com.ghoul.AmanaFund.specification;

import com.ghoul.AmanaFund.entity.CaseStatus;
import com.ghoul.AmanaFund.entity.CaseType;
import com.ghoul.AmanaFund.entity.FraudCases;
import com.ghoul.AmanaFund.entity.StatusAudit;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public class CaseSpecification {

    public static Specification<FraudCases> searchFraudCases(
            String caseType, LocalDateTime detectionDateTime, String caseStatus, Integer userId, Integer auditId) {

        return (root, query, criteriaBuilder) -> {
            Specification<FraudCases> spec = Specification.where(null);

            if (caseType != null) {
                CaseType caseType1 = CaseType.valueOf(caseType.toUpperCase());
                spec = spec.and((root1, query1, cb) -> cb.equal(root1.get("caseType"), caseType1));
            }
            if (detectionDateTime != null) {
                spec = spec.and((root1, query1, cb) -> cb.equal(root1.get("detectionDateTime"), detectionDateTime));
            }
            if (caseStatus != null) {
                CaseStatus caseStatus1 = CaseStatus.valueOf(caseStatus.toUpperCase());
                spec = spec.and((root1, query1, cb) -> cb.equal(root1.get("caseStatus"), caseStatus1));
            }
            if (userId != null) {
                spec = spec.and((root1, query1, cb) -> cb.equal(root1.get("responsibleUser").get("id"), userId));
            }
            if (auditId != null) {
                spec = spec.and((root1, query1, cb) -> cb.equal(root1.get("audit").get("auditId"), auditId));
            }

            return spec.toPredicate(root, query, criteriaBuilder);
        };
    }
}
