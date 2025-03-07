package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.AccountPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AccountPaymentRepository extends JpaRepository<AccountPayment, Integer> {
    List<AccountPayment> findByAgencyName(String agencyName);
    List<AccountPayment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    List<AccountPayment> findByRibAndPaymentDateBetween(String rib, LocalDate startDate, LocalDate endDate);
    // Nouvelle méthode pour trouver les paiements par RIB
    List<AccountPayment> findByRib(String rib);
}