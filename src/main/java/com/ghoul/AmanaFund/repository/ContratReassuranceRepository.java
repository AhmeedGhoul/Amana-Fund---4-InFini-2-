package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.ContratReassurance;
import com.ghoul.AmanaFund.entity.Role;
import com.ghoul.AmanaFund.entity.Sinistre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContratReassuranceRepository  extends JpaRepository<ContratReassurance,Long>,JpaSpecificationExecutor<ContratReassurance> {
}
