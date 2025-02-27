package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.CaseStatus;
import com.ghoul.AmanaFund.entity.FraudCases;
import com.ghoul.AmanaFund.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FraudCaseRepository extends JpaRepository<FraudCases,Integer>{
    Optional<FraudCases> findById(int id) ;

    List<FraudCases> findByResponsibleUserAndCaseStatus(Users responsibleUser, CaseStatus caseStatus);

}