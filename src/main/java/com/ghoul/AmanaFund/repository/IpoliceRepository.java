package com.ghoul.AmanaFund.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ghoul.AmanaFund.entity.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository

public interface IpoliceRepository extends JpaRepository<Police,Long>{
    @Query("SELECT p FROM Police p WHERE " +
            "(:amount IS NULL OR p.amount = :amount)")
    List<Police> searchPolice(
            @Param("amount") Double amount
    );

//    List<Police> findByEndBetween(Date start, Date end);
}
