package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.AccountPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountPaymentRepository extends JpaRepository<AccountPayment,Integer> {
    Optional<AccountPayment> findById(Integer id);
}
