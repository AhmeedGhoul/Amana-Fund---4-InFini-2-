package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.entity.AccountType;
import com.ghoul.AmanaFund.service.IAccountService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import jakarta.validation.Valid;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/Account")
public class AccountController {
    IAccountService accountService;

    @PostMapping("/addaccount")
    public Account ajouterAccount(@Valid @RequestBody Account account) {
        return accountService.AddAccount(account);
    }

    @GetMapping("/dispaccount")
    public List<Account> retrieveBlocs() {
        return accountService.retrieveAccount();
    }

    @GetMapping("/dispaccount/paged")
    public Page<Account> retrieveAccountsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return accountService.retrieveAccount(pageable);
    }

    @PutMapping("/updateaccount")
    public Account updateAccount(@Valid @RequestBody Account account) {
        return accountService.updateAccount(account);
    }

    @DeleteMapping("/delaccount/{id}")
    public void removeAccount(@PathVariable("id") Integer idAccount) {
        accountService.removeAccount(idAccount);
    }

    @GetMapping("/dispaccountId/{id}")
    public Account retrieveAccount(@PathVariable("id") Integer idAccount) {
        return accountService.retrieveAccount(idAccount);
    }

    @GetMapping("/filter/type")
    public List<Account> filterByType(@RequestParam AccountType accountType) {
        return accountService.findByAccountType(accountType);
    }

    @GetMapping("/filter/amount")
    public List<Account> filterByAmount(@RequestParam Double minAmount) {
        return accountService.findByAmountGreaterThan(minAmount);
    }

    @GetMapping("/{id}/future-value")
    public double calculateFutureValue(
            @PathVariable("id") Integer accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {
        return accountService.calculateFutureValue(accountId, targetDate);
    }

    @GetMapping("/{id}/interest-gained")
    public double calculateInterestGained(
            @PathVariable("id") Integer accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {
        return accountService.calculateInterestGained(accountId, targetDate);
    }
    @GetMapping("/export/excel")
    public ResponseEntity<Resource> exportAccountsToExcel() throws IOException {
        List<Account> accounts = accountService.retrieveAccount(); // Récupérer tous les comptes

        ByteArrayInputStream in = accountService.exportAccountsToExcel(accounts);

        // Configurer les headers pour le téléchargement
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=accounts.xlsx");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        // Retourner le fichier comme ressource
        InputStreamResource resource = new InputStreamResource(in);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}