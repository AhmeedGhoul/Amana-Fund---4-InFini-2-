package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Role;
import com.ghoul.AmanaFund.entity.Sinistre;
import com.ghoul.AmanaFund.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SinistreRepository  extends JpaRepository<Sinistre, Long> , JpaSpecificationExecutor<Sinistre> {
    List<Sinistre> findByUser(Users user);}
