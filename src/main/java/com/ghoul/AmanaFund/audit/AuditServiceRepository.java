package com.ghoul.AmanaFund.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuditServiceRepository extends JpaRepository<Audit, Integer> {
    Optional<Audit> findById(Integer id);
}
