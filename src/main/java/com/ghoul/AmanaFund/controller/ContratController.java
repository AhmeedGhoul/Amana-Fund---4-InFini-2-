package com.ghoul.AmanaFund.controller;
import java.util.stream.Collectors;
import com.ghoul.AmanaFund.entity.Contract;
import com.ghoul.AmanaFund.entity.CreditPool;
import com.ghoul.AmanaFund.service.IContractService;
import com.ghoul.AmanaFund.service.ICreaditPoolService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor

public class ContratController {

    private final IContractService iContractService;

    private final ICreaditPoolService iCreaditPoolService;
    @PostMapping("/add")
    public ResponseEntity<?> addContract(@RequestBody Contract contract) {
        try {
            Contract savedContract = iContractService.AddContract(contract);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedContract);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Contract>> getAllContracts() {
        List<Contract> contracts = iContractService.retriveContract();
        return ResponseEntity.ok(contracts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContractById(@PathVariable int id) {
        Contract contract = iContractService.retriveContractById(id);
        if (contract != null) {
            return ResponseEntity.ok(contract);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contract not found");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateContract(@RequestBody Contract contract) {
        Contract updatedContract = iContractService.updateContract(contract);
        return ResponseEntity.ok(updatedContract);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteContract(@PathVariable int id) {
        iContractService.removeContract(id);
        return ResponseEntity.ok("Contract deleted successfully");
    }
    @GetMapping("/{contractId}/recovery-options")
    public ResponseEntity<?> getRecoveryOptionsForContract(
            @PathVariable int contractId,
            @RequestParam int creditPoolId) {

        try {
            // Récupérer le contrat par son ID
            Contract contract = iContractService.retriveContractById(contractId);
            if (contract == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contract not found");
            }

            // Récupérer le pool de crédit par son ID
            CreditPool creditPool = iCreaditPoolService.retrieveCreditPool(creditPoolId);
            if (creditPool == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CreditPool not found");
            }

            // Récupérer tous les contrats du pool de crédit
            List<Contract> allContracts = iContractService.retriveContract().stream()
                    .filter(c -> c.getCreditPool().getId_credit_pool() == creditPoolId)
                    .collect(Collectors.toList());

            // Calculer les options de récupération pour le contrat donné
            List<List<Integer>> recoveryOptions = iContractService.getRecoveryOptionsForContract(contract, creditPool, allContracts);

            return ResponseEntity.ok(recoveryOptions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


    }



