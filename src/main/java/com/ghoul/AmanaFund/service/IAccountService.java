package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Account;

import java.util.List;

public interface IAccountService {
    Account AddAccount(Account account);
    List<Account> retrieveAccount();
    Account updateAccount (Account account);
    Account retrieveAccount (Integer idAccount);
    void removeAccount (Integer idAccount);
}
