package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.FraudCases;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FraudCaseRepository extends JpaRepository<FraudCases,Integer>{
    Optional<FraudCases> findById(int id) ;
}