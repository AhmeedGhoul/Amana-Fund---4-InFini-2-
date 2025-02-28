package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Audit;
import com.ghoul.AmanaFund.repository.AuditServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditServiceRepository auditServiceRepository;
    public void save(Audit audit) {
        auditServiceRepository.save(audit);
    }
    public List<Audit> findAll() {
        return auditServiceRepository.findAll();
    }
    public void delete(Audit audit) {
        auditServiceRepository.delete(audit);
    }
    public Audit getAuditByEmail(int id) {
        return auditServiceRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Aduit not found"));
    }
    public void modify(Audit audit) {
        var auditt=getAuditByEmail(audit.getIdAudit());
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
}
