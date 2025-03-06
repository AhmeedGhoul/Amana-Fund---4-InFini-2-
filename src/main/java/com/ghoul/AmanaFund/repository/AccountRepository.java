package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.entity.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findById(Integer id);
    Page<Account> findAll(Pageable pageable);
    List<Account> findByAccountType(AccountType accountType);
    List<Account> findByAmountGreaterThan(Double amount);
    Optional<Account> findByRib(String rib);
}