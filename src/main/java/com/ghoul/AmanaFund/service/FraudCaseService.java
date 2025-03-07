package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.CaseStatus;
import com.ghoul.AmanaFund.entity.CaseType;
import com.ghoul.AmanaFund.entity.FraudCases;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.repository.ActivityLogRepository;
import com.ghoul.AmanaFund.repository.AuditServiceRepository;
import com.ghoul.AmanaFund.repository.FraudCaseRepository;
import com.ghoul.AmanaFund.specification.CaseSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FraudCaseService {
    private final FraudCaseRepository fraudCaseRepository;
    private final ActivityLogRepository activityLogRepository;
    private final AuditServiceRepository auditServiceRepository;

    public void save(FraudCases fraudCases, Users user) {
        fraudCases.setResponsibleUser(user);
    fraudCaseRepository.save(fraudCases);

    }
    public void delete(FraudCases fraudCases) {
        fraudCaseRepository.delete(fraudCases);
    }
    public FraudCases getFraudById(int id) {
        return fraudCaseRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Case not found"));
    }
    public void modify(FraudCases fraudCases) {
        var fraudd= getFraudById(fraudCases.getId_Fraud());
        if(fraudd!=null) {
            var newCase= FraudCases.builder().id_Fraud(fraudd.getId_Fraud())
                    .caseType(fraudCases.getCaseType())
                    .detectionDateTime(fraudCases.getDetectionDateTime())
                    .caseStatus(fraudCases.getCaseStatus())
                    .build();

            fraudCaseRepository.save(newCase);
        }

    }
    public Page<FraudCases> searchFraudCases(
            String caseType, LocalDateTime detectionDateTime, String caseStatus,
            Integer userId, Integer auditId, List<String> sortBy, int page, int size) {

        Specification<FraudCases> spec = CaseSpecification.searchFraudCases(caseType, detectionDateTime, caseStatus, userId, auditId);

        // Default sort by detectionDateTime DESC
        Sort sort = Sort.by(Sort.Direction.DESC, "detectionDateTime");

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

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return fraudCaseRepository.findAll(spec, pageRequest);
    }

    public Page<FraudCases> findAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return fraudCaseRepository.findAll(pageRequest);
    }

    public String generateFraudCaseReport(Users user, String directoryPath, String fileName) throws IOException {
        List<FraudCases> fraudCases = fraudCaseRepository.findByResponsibleUser(user);
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            directoryPath = "C:/Users/ahmed/Downloads";
        }
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "fraud_case_report.xlsx";
        }
        String filePath = directoryPath + "/" + fileName;
        File file = new File(filePath);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Fraud Cases");
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Case Type", "Detection Date", "Case Status", "Responsible User", "Audit ID"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        int rowNum = 1;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (FraudCases fraudCase : fraudCases) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(fraudCase.getCaseType().toString());
            row.createCell(1).setCellValue(fraudCase.getDetectionDateTime().format(dateTimeFormatter));
            row.createCell(2).setCellValue(fraudCase.getCaseStatus().toString());
            row.createCell(3).setCellValue(fraudCase.getResponsibleUser() != null ? fraudCase.getResponsibleUser().getName() : "N/A");
            row.createCell(4).setCellValue(fraudCase.getAudit() != null ? fraudCase.getAudit().getOutput() : "N/A");
        }

        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }
        return filePath;
    }
    public long getTotalFraudCases() {
        return fraudCaseRepository.count();
    }

    public Map<CaseType, Long> getFraudCasesByType() {
        return fraudCaseRepository.findAll().stream()
                .collect(Collectors.groupingBy(FraudCases::getCaseType, Collectors.counting()));
    }

}
