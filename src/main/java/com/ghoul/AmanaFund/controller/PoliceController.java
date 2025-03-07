package com.ghoul.AmanaFund.controller;
import com.ghoul.AmanaFund.repository.IpoliceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ghoul.AmanaFund.entity.*;
import com.ghoul.AmanaFund.service.*;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/police")
public class PoliceController {
    @Autowired
    private PoliceService policeService;
    @Autowired
    private IpoliceRepository ipoliceRepository;
    @Autowired
    private PDFpoliceService pdFpoliceService;
    @PostMapping("/add_police")
    public Police addPolice(@RequestBody Police police)
    {
        return policeService.addPolice(police);
    }
    @GetMapping("/getall_police")
    public List<Police> GetAllPolice()
    {
        return policeService.retrievePolices();
    }
    @GetMapping("/paginated")
    public Page<Police> getPaginatedContracts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "start") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        // Allow only "start" or "end" for sorting
        List<String> allowedSortFields = Arrays.asList("start", "end");
        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field. Choose between 'start' or 'end'.");
        }

        // Apply sorting direction
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return policeService.getAllPaginated(pageable);
    }

    @PutMapping("/update_police")
    public Police updatePolice(@RequestBody Police police)
    {
        return policeService.updatePolice(police);
    }

    @DeleteMapping("/removepolice/{id}")
    public void removePolice(@PathVariable long id) {
        policeService.removePolice(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Police>> searchPolice(
            @RequestParam(value = "start", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam(value = "amount", required = false) Double amount,
            @RequestParam(value = "id", required = false) Long id
    ) {
        try {
            List<Police> results = policeService.searchPolice(start, amount, id);
            if (results.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{id}/contract")
    public ResponseEntity<byte[]> generateContract(@PathVariable Long id) throws IOException {
        Optional<Police> policeOpt = ipoliceRepository.findById(id);
        if (policeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        InputStream signatureImageStream = null;
        try {
            // Example: Loading from classpath
            ClassPathResource resource = new ClassPathResource("signatures/signature.png"); // Adjust path
            if (resource.exists()) {
                signatureImageStream = resource.getInputStream();
            }

            byte[] pdfBytes = pdFpoliceService.generatePoliceContract(policeOpt.get(), signatureImageStream);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contract_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } finally {
            if (signatureImageStream != null) {
                signatureImageStream.close();
            }
        }
    }
    @GetMapping("/{id}/total-amount-paid")
    public Double getTotalAmountPaid(@PathVariable Long id) {
        return policeService.calculateTotalAmountPaid(id);
    }
    @GetMapping("/{policeId}/next-payment")
    public Date getNextPaymentDate(
            @PathVariable Long policeId,
            @RequestParam("lastPaymentDate") Date lastPaymentDate) {
        return policeService.getNextPaymentDate(policeId, lastPaymentDate);
    }

}
