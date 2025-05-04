package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Request;
import com.ghoul.AmanaFund.entity.Product;
import com.ghoul.AmanaFund.repository.IrequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RequestService implements IrequestService {

    @Autowired
    private IrequestRepository irequestRepository;

    @Autowired
    private NotificationKarimService notificationKarimService;

    @Override
    public Request addRequest(Request request) {
        Request savedRequest = irequestRepository.save(request);
        String requestDetails = formatRequestDetails(savedRequest);
        notificationKarimService.sendEmailNotification(
                "New Request Added - AmanaFund",
                "<h2 style='font-size: 24px; color: #1a73e8;'>A New Request Has Been Added</h2>" +
                        "<p style='font-size: 16px; color: #333;'>We are pleased to inform you that a new request has been added to the system. Below are the details:</p>" + requestDetails,
                "maullamebeba@gmail.com"
        );
        return savedRequest;
    }

    @Override
    public List<Request> retrieveRequests() {
        return List.of();
    }

    public Page<Request> retrieveRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);  // Create a Pageable instance
        return irequestRepository.findAll(pageable);  // Pass it to the repository method
    }
    @Override
    public Request updateRequest(Request request) {
        Request updatedRequest = irequestRepository.save(request);
        String requestDetails = formatRequestDetails(updatedRequest);
        notificationKarimService.sendEmailNotification(
                "Request Updated - AmanaFund",
                "<h2 style='font-size: 24px; color: #1a73e8;'>Request Updated</h2>" +
                        "<p style='font-size: 16px; color: #333;'>A request has been updated. Below are the updated details:</p>" + requestDetails,
                "maullamebeba@gmail.com"
        );
        return updatedRequest;
    }

    @Override
    public Request retrieveRequest(Integer id_request) {
        return irequestRepository.findById(id_request).orElse(null);
    }

    @Override
    public void removeRequest(Integer id_request) {
        irequestRepository.deleteById(id_request);
        notificationKarimService.sendEmailNotification(
                "Request Removed - AmanaFund",
                "<h2 style='font-size: 24px; color: #e74c3c;'>Request Removed</h2>" +
                        "<p style='font-size: 16px; color: #333;'>The request with ID " + id_request + " has been removed from the system. If this was an error, please contact us.</p>",
                "maullamebeba@gmail.com"
        );
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

    private String formatRequestDetails(Request request) {
        StringBuilder details = new StringBuilder();

        details.append("<table style='width: 100%; border-collapse: collapse;'>")
                .append("<tr><td style='padding: 8px; border: 1px solid #ddd;'><strong>Request ID:</strong></td><td style='padding: 8px; border: 1px solid #ddd;'>")
                .append(request.getId_request()).append("</td></tr>")
                .append("<tr><td style='padding: 8px; border: 1px solid #ddd;'><strong>Product:</strong></td><td style='padding: 8px; border: 1px solid #ddd;'>")
                .append(request.getProduct() != null ? request.getProduct() : "N/A").append("</td></tr>")
                .append("<tr><td style='padding: 8px; border: 1px solid #ddd;'><strong>Request Date:</strong></td><td style='padding: 8px; border: 1px solid #ddd;'>")
                .append(request.getDate_Request() != null ? request.getDate_Request() : "N/A").append("</td></tr>")
                .append("<tr><td style='padding: 8px; border: 1px solid #ddd;'><strong>Document:</strong></td><td style='padding: 8px; border: 1px solid #ddd;'>")
                .append(request.getDocument() != null ? request.getDocument() : "N/A").append("</td></tr>")
                .append("</table>");

        return details.toString();
    }
}
