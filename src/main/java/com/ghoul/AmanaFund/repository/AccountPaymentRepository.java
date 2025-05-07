package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.AccountPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AccountPaymentRepository extends JpaRepository<AccountPayment, Integer> {
    List<AccountPayment> findByAgencyName(String agencyName);
    List<AccountPayment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    List<AccountPayment> findByRib(String rib);

    // Paginated versions
    Page<AccountPayment> findByAgencyName(String agencyName, Pageable pageable);
    Page<AccountPayment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<AccountPayment> findByRib(String rib, Pageable pageable);
    Page<AccountPayment> findByPaymentDateBetweenAndRib(LocalDate startDate, LocalDate endDate, String rib, Pageable pageable);
    Page<AccountPayment> findByPaymentDateBetweenAndAgencyName(LocalDate startDate, LocalDate endDate, String agencyName, Pageable pageable);
    Page<AccountPayment> findByRibAndAgencyName(String rib, String agencyName, Pageable pageable);
    Page<AccountPayment> findByPaymentDateBetweenAndRibAndAgencyName(LocalDate startDate, LocalDate endDate, String rib, String agencyName, Pageable pageable);
    // Add this method
    List<AccountPayment> findByRibAndPaymentDateBetween(String rib, LocalDate startDate, LocalDate endDate);

    // If you need pagination version
    Page<AccountPayment> findByRibAndPaymentDateBetween(String rib, LocalDate startDate, LocalDate endDate, Pageable pageable);
}