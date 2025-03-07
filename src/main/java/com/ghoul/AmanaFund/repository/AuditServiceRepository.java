package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.entity.Audit;
import com.ghoul.AmanaFund.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface AuditServiceRepository extends JpaRepository<Audit, Integer>, JpaSpecificationExecutor<Audit> {
    Optional<Audit> findById(Integer id);


}
