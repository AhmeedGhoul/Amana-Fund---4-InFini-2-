package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Request;
import com.ghoul.AmanaFund.entity.Product;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IrequestService {
    Request addRequest(Request request);
    List<Request> retrieveRequests();
    Request updateRequest(Request request);
    Request retrieveRequest(Integer id_request);
    void removeRequest(Integer id_request);
    List<Request> filterByProduct(Product product);
    List<Request> filterByDateRange(LocalDate startDate, LocalDate endDate);
    Page<Request> getRequestsWithPagination(int page, int size);
}