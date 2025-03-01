package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Audit;
import com.ghoul.AmanaFund.entity.CaseStatus;
import com.ghoul.AmanaFund.entity.FraudCases;
import com.ghoul.AmanaFund.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface FraudCaseRepository extends JpaRepository<FraudCases,Integer>, JpaSpecificationExecutor<FraudCases> {
    Optional<FraudCases> findById(int id) ;

    List<FraudCases> findByResponsibleUserAndCaseStatus(Users responsibleUser, CaseStatus caseStatus);
    List<FraudCases> findByResponsibleUser (Users responsibleUser);
}