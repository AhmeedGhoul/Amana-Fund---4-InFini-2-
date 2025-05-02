package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Notification;
import com.ghoul.AmanaFund.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndSeenFalse(Users user);
}