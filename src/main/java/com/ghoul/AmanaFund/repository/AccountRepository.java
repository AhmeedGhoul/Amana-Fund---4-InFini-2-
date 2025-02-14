package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Integer> {
    Optional<Account> findById(Integer id);
}
