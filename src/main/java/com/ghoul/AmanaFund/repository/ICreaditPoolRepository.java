package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.CreditPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository

public interface ICreaditPoolRepository extends JpaRepository<CreditPool,Integer> {

}
