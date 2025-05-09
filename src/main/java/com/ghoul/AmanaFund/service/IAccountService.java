package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.entity.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface IAccountService {
    Account AddAccount(Account account);

    double calculateFutureValue(Integer accountId, LocalDate targetDate);

    double calculateInterestGained(Integer accountId, LocalDate targetDate);

    ByteArrayInputStream exportAccountsToExcel(List<Account> accounts) throws IOException;

    List<Account> retrieveAccount();

    Page<Account> retrieveAccount(Pageable pageable);

    Account updateAccount(Account account);

    Account retrieveAccount(Integer idAccount);

    void removeAccount(Integer idAccount);

    List<Account> findByAccountType(AccountType accountType);

    List<Account> findByAmountGreaterThan(Double amount);

    List<Account> getZakatEligibleAccounts();

    Account addZakatTransaction(Integer accountId, Double zakatAmount);

    void checkNissabStatus(Account account);

    List<Account> getPoorAccountsSorted(int year);

    void distributeZakatToPoorAccounts(int year);

    void distributeZakatAutomatically();

    String generateUniqueRib();

    Account retrieveAccountByRib(String rib);

    Account updateAccount(String rib, Account updatedAccount);

    ByteArrayInputStream generateZakatStatusPDF(Integer accountId, LocalDate checkDate) throws IOException;
}