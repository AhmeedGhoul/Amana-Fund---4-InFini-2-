package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Audit;
import com.ghoul.AmanaFund.entity.Users;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> ,JpaSpecificationExecutor<Users> {
Optional<Users> findByEmail(String email);
int countByEnabled(boolean enabled);
}
