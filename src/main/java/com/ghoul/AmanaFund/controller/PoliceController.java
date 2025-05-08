package com.ghoul.AmanaFund.controller;
import com.ghoul.AmanaFund.DTO.PoliceDTO;
import com.ghoul.AmanaFund.repository.IpoliceRepository;
import com.ghoul.AmanaFund.security.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/police")
public class PoliceController {
    private final JwtService jwtService;
    private final AuthenticationService authService;
    private final PoliceDTOMapper policeDTOMapper;
    @Autowired
    private PoliceService policeService;
    @Autowired
    private IpoliceRepository ipoliceRepository;
    @Autowired
    private PDFpoliceService pdFpoliceService;
    @PostMapping("/add_police")
    public Police addPolice(@Valid @RequestBody Police police /*, @RequestHeader("Authorization") String token*/)
    {
       /* Users adminUser = extractUser(token);
        police.setUser(adminUser);*/
        return policeService.addPolice(police);
    }
    @GetMapping("/getall_police")
    public List<PoliceDTO> GetAllPolice()
    {
        return policeService.retrievePolices();
    }
    @GetMapping("/get_policeById/{id}")
    public PoliceDTO GetPoliceById(@PathVariable Long id)
    {
        return policeService.retrievePolice(id);
    }
    @GetMapping("/active-total-amount")
    public double getTotalActivePoliceAmount() {
        return policeService.calculateTotalActivePoliceAmount();
    }
    @GetMapping("/amount-by-start-date")
    public Map<Date, Double> getAmountByStartDate() {
        return policeService.getAmountSumByStartDate();
    }
    @GetMapping("/total-amount")
    public double getTotalAmount() {
        return policeService.getTotalPoliceAmount();
    }

    @GetMapping("/paginated")
    public Page<PoliceDTO> getPaginatedContracts(
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
    @GetMapping("/active-percentage")
    public double getActivePolicePercentage() {
        return policeService.getActivePolicePercentage();
    }
    @GetMapping("/{id}/guaranteed-amount")
    public double getGuaranteedAmount(@PathVariable Long id) {
        return policeService.calculateGuaranteedAmount(id);
    }


    @PutMapping("/update_police")
    public Police updatePolice(@RequestBody Police police /*,@RequestHeader("Authorization") String token*/)
    {
        /*Users adminUser = extractUser(token);
        police.setUser(adminUser);*/
        return policeService.updatePolice(police);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Police> deactivatePolice(@PathVariable Long id) {
        try {
            Police updatedPolice = policeService.deactivatePolice(id);
            return new ResponseEntity<>(updatedPolice, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/removepolice/{id}")
    public void removePolice(@PathVariable long id) {
        policeService.removePolice(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PoliceDTO>> searchPolice(
            @RequestParam(value = "amount", required = false) Double amount
    ) {
        try {
            List<Police> results = policeService.searchPolice(amount);
            if (results.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            List<PoliceDTO> dtoList = results.stream()
                    .map(policeDTOMapper)
                    .toList();
            return new ResponseEntity<>(dtoList, HttpStatus.OK);
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
            @RequestParam("lastPaymentDate") String lastPaymentDate) {
        try {
            // Define the date format inside the method
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(lastPaymentDate);
            return policeService.getNextPaymentDate(policeId, parsedDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd");
        }
    }
    private Users extractUser(String token) {
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        return authService.getUserByEmail(email);
    }

}
