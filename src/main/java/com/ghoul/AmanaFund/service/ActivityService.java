package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.repository.ActivityLogRepository;
import com.ghoul.AmanaFund.specification.ActivityLogSpecification;
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
public class ActivityService {
    private final ActivityLogRepository activityLogRepository;
    public void save(ActivityLog activityLog) {
        activityLogRepository.save(activityLog);
    }
    public Page<ActivityLog> findAll(Pageable pageable) {
        return activityLogRepository.findAll(pageable);
    }
    public Page<ActivityLog> searchActivityLogs(
            String activityName, String activityDescription, LocalDateTime activityDate,
            Integer userId, Integer auditId, List<String> sortBy, int page, int size) {
        Specification<ActivityLog> spec = ActivityLogSpecification.searchActivityLogs(activityName, activityDescription, activityDate, userId, auditId);
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

        Pageable pageable = PageRequest.of(page, size, sort);
        return activityLogRepository.findAll(spec, pageable);
    }
    public String generateActivityLogReport(Users user, String directoryPath, String fileName) throws IOException {
        List<ActivityLog> activityLogs = activityLogRepository.findByUser(user);
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            directoryPath = "C:/Users/ahmed/Downloads";
        }
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "activity_log_report.xlsx";
        }
        String filePath = directoryPath + "/" + fileName;
        File file = new File(filePath);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Activity Log");
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Activity Name", "Activity Description", "Activity Date", "User Name", "Audit ID"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        int rowNum = 1;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (ActivityLog activityLog : activityLogs) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(activityLog.getActivityName());
            row.createCell(1).setCellValue(activityLog.getActivityDescription());
            row.createCell(2).setCellValue(activityLog.getActivityDate().format(dateTimeFormatter));
            row.createCell(3).setCellValue(activityLog.getUser() != null ? activityLog.getUser().getName() : "N/A");
            row.createCell(4).setCellValue(activityLog.getAudit() != null ? activityLog.getAudit().getOutput() : "N/A");
        }
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }

        return filePath;
    }
    public long getTotalActivities() {
        return activityLogRepository.count();
    }

    public Map<String, Long> getMostCommonActivity() {
        return activityLogRepository.findAll().stream()
                .collect(Collectors.groupingBy(ActivityLog::getActivityName, Collectors.counting()));
    }
}
