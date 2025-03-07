package com.ghoul.AmanaFund.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ghoul.AmanaFund.entity.*;

import java.util.Date;
import java.util.List;

@Repository

public interface IpoliceRepository extends JpaRepository<Police,Long>{
    @Query("SELECT p FROM Police p WHERE " +
            "(:start IS NULL OR p.start = :start) AND " +
            "(:amount IS NULL OR p.amount = :amount) AND " +
            "(:id IS NULL OR p.idPolice = :id)")
    List<Police> searchPolice(
            @Param("start") Date start,
            @Param("amount") Double amount,
            @Param("id") Long id
    );
}
