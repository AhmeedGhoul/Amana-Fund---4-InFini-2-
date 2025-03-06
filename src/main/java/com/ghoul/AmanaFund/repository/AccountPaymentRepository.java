package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.AccountPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountPaymentRepository extends JpaRepository<AccountPayment, Integer> {
    Optional<AccountPayment> findById(Integer id);
    Page<AccountPayment> findAll(Pageable pageable);
    List<AccountPayment> findByAgencyName(String agencyName);
    List<AccountPayment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    List<AccountPayment> findByRib(String rib); // Méthode nécessaire pour le calcul
}