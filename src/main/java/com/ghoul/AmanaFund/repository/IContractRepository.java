package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IContractRepository extends JpaRepository<Contract,Integer> {

}
