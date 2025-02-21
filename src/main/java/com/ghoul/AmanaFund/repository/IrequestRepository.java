package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IrequestRepository extends JpaRepository<Request,Integer>
{
}
