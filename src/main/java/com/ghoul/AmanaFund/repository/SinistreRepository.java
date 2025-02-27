package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Role;
import com.ghoul.AmanaFund.entity.Sinistre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SinistreRepository  extends JpaRepository<Sinistre, Long> {
}
