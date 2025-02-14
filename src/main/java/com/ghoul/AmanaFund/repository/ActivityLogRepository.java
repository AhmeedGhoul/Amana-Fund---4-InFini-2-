package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Integer> {
    Optional<ActivityLog> findById(Integer id);
}
