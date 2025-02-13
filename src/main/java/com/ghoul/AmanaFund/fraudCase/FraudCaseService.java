package com.ghoul.AmanaFund.fraudCase;

import com.ghoul.AmanaFund.audit.Audit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public void modify(FraudCases fraudCases) {
        var fraudCasess=fraudCaseRepository.findById(fraudCases.getId_Fraud());
        if(fraudCasess!=null) {
            fraudCaseRepository.save(fraudCases);}
    }
}
