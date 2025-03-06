package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.entity.Users;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Integer> , JpaSpecificationExecutor<ActivityLog> {
    Optional<ActivityLog> findById(Integer id);
    List<ActivityLog> findByUser(Users user);
}
