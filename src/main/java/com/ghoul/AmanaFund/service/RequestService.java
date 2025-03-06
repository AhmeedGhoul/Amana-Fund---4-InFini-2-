package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Request;
import com.ghoul.AmanaFund.entity.Product;
import com.ghoul.AmanaFund.repository.IrequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestService implements IrequestService {
    @Autowired
    private IrequestRepository irequestRepository;

    @Override
    public Request addRequest(Request request) {
        return irequestRepository.save(request);
    }

    @Override
    public List<Request> retrieveRequests() {
        return irequestRepository.findAll();
    }

    @Override
    public Request updateRequest(Request request) {
        return irequestRepository.save(request);
    }

    @Override
    public Request retrieveRequest(Integer id_request) {
        return irequestRepository.findById(id_request).orElse(null);
    }

    @Override
    public void removeRequest(Integer id_request) {
        irequestRepository.deleteById(id_request);
    }

    @Override
    public List<Request> filterByProduct(Product product) {
        return irequestRepository.findByProduct(product);
    }

    @Override
    public List<Request> filterByDateRange(LocalDate startDate, LocalDate endDate) {
        return irequestRepository.findByDateRange(startDate, endDate);
    }

    @Override
    public Page<Request> getRequestsWithPagination(int page, int size) {
        return irequestRepository.findAll(PageRequest.of(page - 1, size));
    }
}