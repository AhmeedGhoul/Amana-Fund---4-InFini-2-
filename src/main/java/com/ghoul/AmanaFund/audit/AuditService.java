package com.ghoul.AmanaFund.audit;

import com.ghoul.AmanaFund.activityLog.ActivityLog;
import com.ghoul.AmanaFund.activityLog.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
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
    public void modify(Audit audit) {
        var auditt=auditServiceRepository.findById(audit.getIdAudit());
        if(auditt!=null) {
        auditServiceRepository.save(audit);}
    }
    public List<Audit> getAllAudit() {
        return auditServiceRepository.findAll();
    }
}
