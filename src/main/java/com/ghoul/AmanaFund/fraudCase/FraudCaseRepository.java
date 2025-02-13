package com.ghoul.AmanaFund.fraudCase;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FraudCaseRepository extends JpaRepository<FraudCases,Integer>{
    Optional<FraudCases> findById(int id) ;
}