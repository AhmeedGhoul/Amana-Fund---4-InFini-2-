package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Contract;
import com.ghoul.AmanaFund.entity.CreditPool;
import com.ghoul.AmanaFund.repository.ICreaditPoolRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public  class CreditPoolService implements ICreaditPoolService {

    private ICreaditPoolRepository iCreditPoolRepository;

    
    private static final double TMM = 0.08; // TMM de 8 %


    @Override
    public CreditPool addCreditPool(CreditPool creditPool) throws ValidationException {
        if (creditPool == null) {
            throw new ValidationException("CreditPool cannot be null");
        }

        // Manually validate minValue < maxValue
        if (creditPool.getMinValue() >= creditPool.getMaxValue()) {
            throw new ValidationException("Min value must be less than Max value");
        }

        // Manually validate openDate before closeDate and after the current date
        LocalDateTime now = LocalDateTime.now();
        if (creditPool.getOpen_Date().isBefore(now)) {
            throw new ValidationException("Open date must be after the current date");
        }

        if (creditPool.getOpen_Date().isAfter(creditPool.getClose_Date())) {
            throw new ValidationException("Open date must be before close date");
        }

        // Save the CreditPool if all validations pass
        return iCreditPoolRepository.save(creditPool);
    }

    @Override
    public List<CreditPool> retrieveCreditPools() {
        return iCreditPoolRepository.findAll();    }

    @Override
    public CreditPool updateCreditPool(CreditPool creditPool) {
        return iCreditPoolRepository.save(creditPool);
    }

    @Override
    public CreditPool retrieveCreditPool(int idPool) {
        return iCreditPoolRepository.findById(idPool).orElse(null);
    }

    @Override
    public void removeCreditPool(int idPool) {
        iCreditPoolRepository.deleteById(idPool);

    }

    @Override
    public Map<Integer, Double> calculateInterestRatesForPool(CreditPool creditPool) {
        Map<Integer, Double> interestRates = new HashMap<>();

        // Récupérer tous les contrats du pool
        List<Contract> contracts = creditPool.getContracts();

        // Calculer le taux d'intérêt pour chaque contrat
        for (Contract contract : contracts) {
            double amount = contract.getAmount();
            int queueNumber = contract.getQueue_Number();
            int nEcheance = creditPool.getN_Echeance();

            // Calculer le facteur de risque
            double riskFactor = amount / (nEcheance * 1000.0);

            // Calculer le taux d'intérêt
            double interestRate = TMM + (riskFactor * queueNumber);

            // Ajouter le taux d'intérêt à la map
            interestRates.put(contract.getId_Contract(), interestRate * 100); // Convertir en pourcentage
        }

        return interestRates;
    }
    }




