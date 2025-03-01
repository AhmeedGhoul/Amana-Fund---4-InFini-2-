package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Integer> , JpaSpecificationExecutor<ActivityLog> {
    Optional<ActivityLog> findById(Integer id);
}
