package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.entity.Users;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Integer> , JpaSpecificationExecutor<ActivityLog> {
    Optional<ActivityLog> findById(Integer id);
    List<ActivityLog> findByUser(Users user);
    @Query("SELECT l FROM ActivityLog l JOIN FETCH l.user WHERE l.activityDate >= :timeLimit ORDER BY l.activityDate DESC")
    List<ActivityLog> getRecentLogs(LocalDateTime timeLimit);
}
