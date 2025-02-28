package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

Optional<Role> findByName(String role);
}
