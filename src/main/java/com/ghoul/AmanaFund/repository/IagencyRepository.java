package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Agency;
import com.ghoul.AmanaFund.entity.Governorate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IagencyRepository extends JpaRepository<Agency, Integer> {

    // Advanced search: partial match for city names
    @Query("SELECT a FROM Agency a WHERE LOWER(a.city) LIKE LOWER(CONCAT('%', :city, '%'))")
    List<Agency> searchByCity(@Param("city") String city);

    // Filtering by governorate
    @Query("SELECT a FROM Agency a WHERE a.governorate = :governorate")
    List<Agency> findByGovernorate(@Param("governorate") Governorate governorate);

    // Pagination
    Page<Agency> findAll(Pageable pageable);
}