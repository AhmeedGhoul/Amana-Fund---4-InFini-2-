package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class accountService implements IAccountService {
    AccountRepository accountRepository;
    @Override
    public Account AddAccount(Account account) {
        accountRepository.save(account);
        return account;
    }

    @Override
    public List<Account> retrieveAccount() {

        return accountRepository.findAll();
    }

    @Override
    public Account updateAccount(Account account) {

        return accountRepository.save(account);
    }

    @Override
    public Account retrieveAccount(Integer idAccount) {
        return accountRepository.findById(idAccount).get();
    }

    @Override
    public void removeAccount(Integer idAccount) {
        accountRepository.deleteById(idAccount);

    }

}
