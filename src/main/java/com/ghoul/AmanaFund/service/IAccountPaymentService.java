package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.dto.PaymentStatisticsDTO;
import com.ghoul.AmanaFund.entity.AccountPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IAccountPaymentService {
    AccountPayment AddAccountPayment(AccountPayment accountPayment);
    List<AccountPayment> retrieveAccountPayment();
    Page<AccountPayment> retrieveAccountPayment(Pageable pageable);
    AccountPayment updateAccountPayment(AccountPayment accountPayment);
    void removeAccountPayment(Integer idAccountPayment);
    AccountPayment retrieveAccountPayment(Integer idAccountPayment);
    List<AccountPayment> findByAgencyName(String agencyName);
    List<AccountPayment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    List<AccountPayment> findByRib(String rib);
    List<PaymentStatisticsDTO> getPaymentStatistics(String rib, String periodType);

    // New methods for enhanced filtering
    Page<AccountPayment> getPaymentsWithFilters(LocalDate startDate, LocalDate endDate, String rib, String agencyName, Pageable pageable);
    Page<AccountPayment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<AccountPayment> findByRib(String rib, Pageable pageable);
    Page<AccountPayment> findByAgencyName(String agencyName, Pageable pageable);
}