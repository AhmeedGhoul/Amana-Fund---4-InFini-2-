package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.dto.PaymentStatisticsDTO;
import com.ghoul.AmanaFund.entity.AccountPayment;
import com.ghoul.AmanaFund.repository.AccountPaymentRepository;
import com.ghoul.AmanaFund.repository.AccountRepository;
import com.ghoul.AmanaFund.service.EmailService;
import com.ghoul.AmanaFund.service.IAccountPaymentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/account-payments")
public class AccountPaymentController {
    IAccountPaymentService accountPaymentService;
    EmailService emailService;
    AccountRepository accountRepository;
    AccountPaymentRepository accountPaymentRepository;

    // Existing endpoints remain exactly the same
    @PostMapping("/addaccountpayment")
    public AccountPayment ajouterAccountPayment(@Valid @RequestBody AccountPayment accountPayment) {
        return accountPaymentService.AddAccountPayment(accountPayment);
    }

    @GetMapping("dispaccountpayment")
    public List<AccountPayment> retrieveAccountPayment() {
        return accountPaymentService.retrieveAccountPayment();
    }

    @GetMapping("/dispaccountpayment/paged")
    public Page<AccountPayment> retrieveAccountPaymentsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return accountPaymentService.retrieveAccountPayment(pageable);
    }

    @PutMapping("updateaccountpayment")
    public AccountPayment updateAccounPayment(@Valid @RequestBody AccountPayment accountPayment) {
        return accountPaymentService.updateAccountPayment(accountPayment);
    }

    @DeleteMapping("delaccountpayment/{id}")
    public void removeAccountPayment(@PathVariable("id") Integer idAccountPayment) {
        accountPaymentService.removeAccountPayment(idAccountPayment);
    }

    @GetMapping("dispaccountpaymentId/{id}")
    public AccountPayment retrieveAccountPayment(@PathVariable("id") Integer idAccountPayment) {
        return accountPaymentService.retrieveAccountPayment(idAccountPayment);
    }

    @GetMapping("/filter/agency")
    public List<AccountPayment> filterByAgency(@RequestParam String agencyName) {
        return accountPaymentService.findByAgencyName(agencyName);
    }

    @GetMapping("/filter/date")
    public List<AccountPayment> filterByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return accountPaymentService.findByPaymentDateBetween(startDate, endDate);
    }

    @GetMapping("/by-rib/{rib}")
    public List<AccountPayment> getAccountPaymentsByRib(@PathVariable String rib) {
        return accountPaymentService.findByRib(rib);
    }

    @GetMapping("/payment-statistics")
    public ResponseEntity<?> getPaymentStats(
            @RequestParam String rib,
            @RequestParam String periodType
    ) {
        try {
            List<PaymentStatisticsDTO> stats = accountPaymentService.getPaymentStatistics(rib, periodType);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // New endpoints added below - keeping all existing code exactly the same

    @GetMapping("/filter/advanced")
    public Page<AccountPayment> advancedFilterPayments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String rib,
            @RequestParam(required = false) String agencyName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentDate,desc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, parseSortOrders(sort));
        return accountPaymentService.getPaymentsWithFilters(startDate, endDate, rib, agencyName, pageable);
    }

    @GetMapping("/by-rib-paged/{rib}")
    public Page<AccountPayment> getPaymentsByRibPaginated(
            @PathVariable String rib,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return accountPaymentService.findByRib(rib, pageable);
    }

    @GetMapping("/filter/date-paged")
    public Page<AccountPayment> filterByDateRangePaginated(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return accountPaymentService.findByPaymentDateBetween(startDate, endDate, pageable);
    }

    @GetMapping("/filter/agency-paged")
    public Page<AccountPayment> filterByAgencyPaginated(
            @RequestParam String agencyName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return accountPaymentService.findByAgencyName(agencyName, pageable);
    }

    private Sort parseSortOrders(String[] sort) {
        return Sort.by(Arrays.stream(sort)
                .map(this::parseOrder)
                .toArray(Sort.Order[]::new));
    }

    private Sort.Order parseOrder(String sortString) {
        String[] parts = sortString.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid sort parameter: " + sortString +
                    ". Expected format: property,direction");
        }
        String property = parts[0];
        Sort.Direction direction = Sort.Direction.fromString(parts[1]);
        return new Sort.Order(direction, property);
    }
}