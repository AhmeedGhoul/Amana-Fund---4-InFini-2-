package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Request;
import com.ghoul.AmanaFund.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IrequestRepository extends JpaRepository<Request, Integer> {

    // Filtering by product
    @Query("SELECT r FROM Request r WHERE r.product = :product")
    List<Request> findByProduct(@Param("product") Product product);

    // Filtering by date range
    @Query("SELECT r FROM Request r WHERE r.date_Request BETWEEN :startDate AND :endDate")
    List<Request> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Pagination support
    Page<Request> findAll(Pageable pageable);
}
