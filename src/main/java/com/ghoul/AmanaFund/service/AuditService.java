package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Audit;
import com.ghoul.AmanaFund.repository.AuditServiceRepository;
import com.ghoul.AmanaFund.specification.AuditSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditServiceRepository auditServiceRepository;
    public void save(Audit audit) {
       auditServiceRepository.save(audit);

    }

    public void delete(Audit audit) {
        auditServiceRepository.delete(audit);

    }
    public Audit getAuditById(int id) {
        return auditServiceRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Audit not found"));
    }
    public void modify(Audit audit) {
        var auditt= getAuditById(audit.getIdAudit());
        if(auditt!=null) {
            var newAudit= Audit.builder().idAudit(auditt.getIdAudit())
                            .dateAudit(audit.getDateAudit())
                                    .statusAudit(audit.getStatusAudit())
                                            .auditType(audit.getAuditType())
                                                    .reviewedDate(audit.getReviewedDate())
                    .output(audit.getOutput())
                    .build();

        auditServiceRepository.save(newAudit);
        }

    }
    public List<Audit> getAllAudit() {
        return auditServiceRepository.findAll();
    }
    public List<Audit> searchAudits(LocalDateTime dateAudit, String statusAudit, String output, LocalDateTime reviewedDate, String auditType, List<String> sortBy) {
        Specification<Audit> spec = AuditSpecification.searchAudit(dateAudit, statusAudit, output, reviewedDate, auditType);
        Sort sort = Sort.by(Sort.Direction.DESC, "dateAudit");
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

        return auditServiceRepository.findAll(spec, sort);
    }
}
