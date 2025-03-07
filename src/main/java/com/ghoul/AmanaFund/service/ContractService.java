package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Contract;
import com.ghoul.AmanaFund.entity.CreditPool;
import com.ghoul.AmanaFund.entity.Payment;
import com.ghoul.AmanaFund.repository.IContractRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ContractService implements IContractService{
   @Autowired
    private IContractRepository iContractRepository;

    @Override
    public Contract AddContract(Contract contract) {

        // Ensure the contract date is not in the past
        if (contract.getDate_Contract().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Contract date must not be in the past");
        }
        return iContractRepository.save(contract);
    }

    @Override
    public List<Contract> retriveContract() {
        return iContractRepository.findAll();
    }

    @Override
    public Contract updateContract(Contract contract) {
        return iContractRepository.save(contract);
    }

    @Override
    public Contract retriveContractById(int id_contract) {
        return iContractRepository.findById(id_contract).orElse(null);
    }

    @Override
    public void removeContract(int id_contract) {
        iContractRepository.deleteById(id_contract);
    }

    @Override
    public List<List<Integer>> getRecoveryOptionsForContract(Contract contract, CreditPool creditPool, List<Contract> allContracts) {
        List<List<Integer>> recoveryOptions = new ArrayList<>();

        // Simulation des mois jusqu'au nombre d'échéances
        for (int month = 1; month <= creditPool.getN_Echeance(); month++) {
            double accumulatedFunds = 0;  // Remettre à zéro pour chaque mois

            // Accumuler les paiements de tous les contrats pour ce mois précis
            for (Contract c : allContracts) {
                accumulatedFunds += c.getPayed();
            }

            // Vérifier si le contrat peut être récupéré ce mois-ci
            if (accumulatedFunds >= contract.getAmount()) {
                // Ajouter ce mois comme une option de récupération
                recoveryOptions.add(Collections.singletonList(month));

                // Ajouter des combinaisons avec les mois précédents
                List<List<Integer>> previousOptions = new ArrayList<>(recoveryOptions);
                for (List<Integer> option : previousOptions) {
                    if (!option.contains(month)) {
                        List<Integer> newOption = new ArrayList<>(option);
                        newOption.add(month);
                        recoveryOptions.add(newOption);
                    }
                }
            }
        }

        return recoveryOptions;
    }


    @Override
    public void refactorEcheances(Contract contract, List<Payment> payments) {
        LocalDate today = LocalDate.now();
        CreditPool creditPool = contract.getCreditPool();
        int remainingEcheances = creditPool.getN_Echeance();
        double initialAmount = contract.getAmount();

        for (Payment payment : payments) {
            if (!payment.getStatus()) {
                LocalDate dueDate = payment.getDate_payment().toLocalDate();
                LocalDate gracePeriodEnd = dueDate.plusDays(creditPool.getGrace_Period().toLocalDateTime().getDayOfMonth());

                if (today.isAfter(gracePeriodEnd)) {
                    long daysLate = ChronoUnit.DAYS.between(gracePeriodEnd, today);
                    double penalty = payment.getAmount() * 0.001 * daysLate;
                    double newAmount = payment.getAmount() + penalty;

                    if (remainingEcheances > 1) {
                        double redistributedAmount = newAmount / remainingEcheances;
                        for (Payment p : payments) {
                            if (!p.getStatus()) {
                                p.setAmount(p.getAmount() + redistributedAmount);
                            }
                        }
                    } else {
                        payment.setAmount(newAmount);
                    }
                }
            }
        }
    }
    }



