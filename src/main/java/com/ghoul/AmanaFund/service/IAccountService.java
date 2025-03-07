package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.entity.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAccountService {
    Account AddAccount(Account account);
    List<Account> retrieveAccount();
    Page<Account> retrieveAccount(Pageable pageable);
    Account updateAccount(Account account);
    Account retrieveAccount(Integer idAccount);
    void removeAccount(Integer idAccount);
    List<Account> findByAccountType(AccountType accountType);
    List<Account> findByAmountGreaterThan(Double amount);
}