package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.entity.AccountType;
import com.ghoul.AmanaFund.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class accountService implements IAccountService {
    AccountRepository accountRepository;

    @Override
    public Account AddAccount(Account account) {
        if (account.getRib() == null || account.getRib().isBlank()) {
            account.setRib(generateUniqueRib());
        }
        accountRepository.save(account);
        return account;
    }

    @Override
    public List<Account> retrieveAccount() {
        return accountRepository.findAll();
    }

    @Override
    public Page<Account> retrieveAccount(Pageable pageable) {
        return accountRepository.findAll(pageable);
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

    @Override
    public List<Account> findByAccountType(AccountType accountType) {
        return accountRepository.findByAccountType(accountType);
    }

    @Override
    public List<Account> findByAmountGreaterThan(Double amount) {
        return accountRepository.findByAmountGreaterThan(amount);
    }

    private String generateUniqueRib() {
        String rib;
        do {
            rib = "TN" + String.format("%018d", new Random().nextInt(999999999) + 1000000000);
        } while (accountRepository.findByRib(rib).isPresent());
        return rib;
    }
}