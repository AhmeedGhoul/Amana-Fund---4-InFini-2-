package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.CreditPool;
import com.ghoul.AmanaFund.service.CreditPoolService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/creditpool")
@RequiredArgsConstructor
public class CreditPoolController {
    @Autowired
    private final CreditPoolService creditPoolService;

    @PostMapping("CreateCreditPool")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<CreditPool> addCreditPool(@RequestBody CreditPool creditPool) {
        CreditPool createdCreditPool = creditPoolService.addCreditPool(creditPool);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(createdCreditPool);
    }
    @GetMapping("/all")
    public ResponseEntity<List<CreditPool>> getAllCreditPools() {
        List<CreditPool> pools = creditPoolService.retrieveCreditPools();
        return ResponseEntity.ok(pools);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCreditPool(@PathVariable int id) {
        CreditPool pool = creditPoolService.retrieveCreditPool(id);
        if (pool != null) {
            return ResponseEntity.ok(pool);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Credit Pool not found");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCreditPool(@RequestBody CreditPool creditPool) {
        CreditPool updatedPool = creditPoolService.updateCreditPool(creditPool);
        return ResponseEntity.ok(updatedPool);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCreditPool(@PathVariable int id) {
        creditPoolService.removeCreditPool(id);
        return ResponseEntity.ok("Credit Pool deleted successfully");
    }
    @GetMapping("/{creditPoolId}/interest-rates")
    public ResponseEntity<?> calculateInterestRatesForPool(@PathVariable int creditPoolId) {
        try {
            // Récupérer le pool de crédit par son ID
            CreditPool creditPool = creditPoolService.retrieveCreditPool(creditPoolId);

            // Calculer les taux d'intérêt pour chaque contrat dans le pool
            Map<Integer, Double> interestRates = creditPoolService.calculateInterestRatesForPool(creditPool);

            return ResponseEntity.ok(interestRates);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}
