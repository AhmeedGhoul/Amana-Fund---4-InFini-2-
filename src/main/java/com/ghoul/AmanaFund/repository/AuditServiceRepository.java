package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AuditServiceRepository extends JpaRepository<Audit, Integer>, JpaSpecificationExecutor<Audit> {
    Optional<Audit> findById(Integer id);

}
