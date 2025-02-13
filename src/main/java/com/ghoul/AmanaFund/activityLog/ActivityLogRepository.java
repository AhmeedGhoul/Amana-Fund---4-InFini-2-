package com.ghoul.AmanaFund.activityLog;

import com.ghoul.AmanaFund.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Integer> {
    Optional<ActivityLog> findById(Integer id);
}
