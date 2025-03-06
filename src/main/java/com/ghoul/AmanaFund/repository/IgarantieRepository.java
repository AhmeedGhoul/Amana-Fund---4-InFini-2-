package com.ghoul.AmanaFund.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ghoul.AmanaFund.entity.*;
import org.springframework.stereotype.Repository;

@Repository

public interface IgarantieRepository extends JpaRepository<Garantie,Long> {
}
