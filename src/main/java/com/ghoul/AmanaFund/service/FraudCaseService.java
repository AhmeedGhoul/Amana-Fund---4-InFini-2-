package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.CaseStatus;
import com.ghoul.AmanaFund.entity.CaseType;
import com.ghoul.AmanaFund.entity.FraudCases;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.repository.FraudCaseRepository;
import com.ghoul.AmanaFund.specification.CaseSpecification;
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
public class FraudCaseService {
    private final FraudCaseRepository fraudCaseRepository;
    public void save(FraudCases fraudCases, Users user) {
        fraudCases.setResponsibleUser(user);
    fraudCaseRepository.save(fraudCases);

    }
    public List<FraudCases> findAll() {
        return fraudCaseRepository.findAll();
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
    public List<FraudCases> getAllCases(){
        return fraudCaseRepository.findAll();
    }
    public List<FraudCases> searchFraudCases(
            String caseType, LocalDateTime detectionDateTime, String caseStatus,
            Integer userId, Integer auditId, List<String> sortBy) {

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

        return fraudCaseRepository.findAll(spec, sort);
    }
}
