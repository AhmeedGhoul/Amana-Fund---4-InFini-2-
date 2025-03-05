package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
Optional<Users> findByEmail(String email);
}
