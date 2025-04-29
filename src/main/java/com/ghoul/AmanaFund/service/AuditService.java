package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.entity.Audit;
import com.ghoul.AmanaFund.entity.FraudCases;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.repository.AuditServiceRepository;
import com.ghoul.AmanaFund.specification.AuditSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditServiceRepository auditServiceRepository;
    public Audit save(Audit audit) {
       auditServiceRepository.save(audit);

        return audit;
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
    public Page<Audit> getAllAudit(Pageable pageable) {
        return auditServiceRepository.findAll(pageable);
    }
    public Page<Audit> searchAudits(String dateAuditStr, String statusAudit, String output, String reviewedDateStr, String auditType, List<String> sortBy, int page, int size) {
        Specification<Audit> spec = AuditSpecification.searchAudit(dateAuditStr, statusAudit, output, reviewedDateStr, auditType);

        // Handle sorting
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

        Pageable pageable = PageRequest.of(page, size, sort);

        return auditServiceRepository.findAll(spec, pageable);
    }

    public String generateAuditReport(Users user, String directoryPath, String fileName) throws IOException {
        List<Audit> audits = auditServiceRepository.findAll();


        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            directoryPath = "C:/Users/ahmed/Downloads";
        }
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "audit_report.xlsx";
        }

        String filePath = directoryPath + "/" + fileName;
        File file = new File(filePath);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Audit Report");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"Audit ID", "Audit Date", "Audit Status", "Output", "Reviewed Date", "Audit Type", "Associated Activity Log", "Associated Fraud Cases"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 1;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Audit audit : audits) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(audit.getIdAudit());
            row.createCell(1).setCellValue(audit.getDateAudit().format(dateTimeFormatter));
            row.createCell(2).setCellValue(audit.getStatusAudit().toString());
            row.createCell(3).setCellValue(audit.getOutput() != null ? audit.getOutput() : "N/A");
            row.createCell(4).setCellValue(audit.getReviewedDate() != null ? audit.getReviewedDate().format(dateTimeFormatter) : "N/A");
            row.createCell(5).setCellValue(audit.getAuditType().toString());

            String activityLogs = audit.getActivityLogs().isEmpty() ? "N/A" : audit.getActivityLogs().stream()
                    .map(ActivityLog::toString)
                    .reduce((s1, s2) -> s1 + ", " + s2).orElse("");
            row.createCell(6).setCellValue(activityLogs);

            String fraudCases = audit.getFraudCases().isEmpty() ? "N/A" : audit.getFraudCases().stream()
                    .map(FraudCases::toString)
                    .reduce((s1, s2) -> s1 + ", " + s2).orElse("");
            row.createCell(7).setCellValue(fraudCases);
        }
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }

        return filePath;
    }

    public long getTotalAudits() {
        return auditServiceRepository.count();
    }
    public long getAuditsWithFraudCases() {
        return auditServiceRepository.findAll().stream().filter(audit -> !audit.getFraudCases().isEmpty()).count();
    }
}
