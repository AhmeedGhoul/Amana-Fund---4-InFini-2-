package com.ghoul.AmanaFund.fraudCase;

import com.ghoul.AmanaFund.audit.Audit;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudCaseService {
    private final FraudCaseRepository fraudCaseRepository;
    public void save(FraudCases fraudCases){
        fraudCaseRepository.save(fraudCases);
    }
    public List<FraudCases> findAll() {
        return fraudCaseRepository.findAll();
    }
    public void delete(FraudCases fraudCases) {
        fraudCaseRepository.delete(fraudCases);
    }
    public FraudCases getFraudByEmail(int id) {
        return fraudCaseRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Case not found"));
    }
    public void modify(FraudCases fraudCases) {
        var fraudd=getFraudByEmail(fraudCases.getId_Fraud());
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
}
