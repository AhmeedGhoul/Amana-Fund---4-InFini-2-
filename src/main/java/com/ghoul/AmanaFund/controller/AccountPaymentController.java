package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.AccountPayment;
import com.ghoul.AmanaFund.service.IAccountPaymentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/account-payments")
public class AccountPaymentController {
    IAccountPaymentService accountPaymentService;

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
}