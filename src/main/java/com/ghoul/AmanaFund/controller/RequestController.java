package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.Product;
import com.ghoul.AmanaFund.service.NotificationService;
import com.ghoul.AmanaFund.service.PdfGenerationService;
import com.ghoul.AmanaFund.service.RequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ghoul.AmanaFund.entity.Request;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("Request")
@Tag(name = "Request")
public class RequestController {
    @Autowired
    private RequestService requestService;

    @Autowired
    private PdfGenerationService pdfGenerationService;

    @PostMapping("add_request")
    public Request addRequest(@Valid @RequestBody Request request) {
        return requestService.addRequest(request);
    }

    @GetMapping("/getall_request")
    public Page<Request> getAllRequests(@RequestParam("page") int page, @RequestParam("size") int size) {
        return requestService.retrieveRequests(page, size);  // Call the service with pagination parameters
    }

    @PutMapping("update_request")
    public Request updateRequest(@Valid @RequestBody Request request) {
        return requestService.updateRequest(request);
    }

    @GetMapping("get_request/{id_request}")
    public Request getRequestById(@PathVariable Integer id_request) {
        return requestService.retrieveRequest(id_request);
    }

    @DeleteMapping("remove_request/{id_request}")
    public void removeRequest(@PathVariable Integer id_request) {
        requestService.removeRequest(id_request);
    }

    // Filtering by product
    @GetMapping("filter/product")
    public List<Request> filterByProduct(@RequestParam Product product) {
        return requestService.filterByProduct(product);
    }

    // Filtering by date range
    @GetMapping("filter/date")
    public List<Request> filterByDateRange(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return requestService.filterByDateRange(startDate, endDate);
    }

    // Pagination
    @GetMapping("paginate")
    public Page<Request> getRequestsWithPagination(@RequestParam int page, @RequestParam int size) {
        return requestService.getRequestsWithPagination(page, size);
    }

    @GetMapping("generate-pdf")
    public ResponseEntity<byte[]> generatePdf(@RequestParam("startDate") String startDateStr,
                                              @RequestParam("endDate") String endDateStr) throws IOException {

        // Convert date strings to LocalDate objects
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        // Generate the PDF
        byte[] pdfBytes = pdfGenerationService.generatePdfForDateRange(startDate, endDate);

        // Set the headers to make it downloadable
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=request_report.pdf");
        headers.add("Content-Type", "application/pdf");

        // Return the PDF in the response body
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

}