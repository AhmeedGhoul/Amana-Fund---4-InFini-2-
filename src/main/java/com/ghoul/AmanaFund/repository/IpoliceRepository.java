package com.ghoul.AmanaFund.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ghoul.AmanaFund.entity.*;
@Repository

public interface IpoliceRepository extends JpaRepository<Police,Long>{
}
