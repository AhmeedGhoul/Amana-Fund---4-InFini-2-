package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.entity.AccountType;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.security.JwtService;
import com.ghoul.AmanaFund.service.AuthenticationService;
import com.ghoul.AmanaFund.service.IAccountService;
import com.ghoul.AmanaFund.service.PdfService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("Account")
@Slf4j
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    IAccountService accountService;
    private final JwtService jwtService;
    private final AuthenticationService authService;
    private final PdfService pdfService;

    @PostMapping("/addaccount")
    public Account ajouterAccount(@Valid @RequestBody Account account, @RequestHeader("Authorization") String token) {
        Users agent = extractUser(token); // Agent is extracted from the token
        account.setAgent(agent); // Renamed from setUser
        account.setClientEmail(account.getClientEmail()); // Ensure clientEmail is set in the request
        return accountService.AddAccount(account);
    }

    @GetMapping("/dispaccount")
    public List<Account> retrieveAccounts() {
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

//    @GetMapping("/{id}/future-value")
//    public ResponseEntity<Double> calculateFutureValue(
//            @PathVariable("id") Integer accountId,
//            @RequestParam("targetDate") String targetDate) {
//        LocalDate date = LocalDate.parse(targetDate);
//        double futureValue = accountService.calculateFutureValue(accountId, date);
//        return ResponseEntity.ok(futureValue);
//    }
//
//    @GetMapping("/{id}/interest-gained")
//    public ResponseEntity<Double> calculateInterestGained(
//            @PathVariable("id") Integer accountId,
//            @RequestParam("targetDate") String targetDate) {
//        LocalDate date = LocalDate.parse(targetDate);
//        double interest = accountService.calculateInterestGained(accountId, date);
//        return ResponseEntity.ok(interest);
//    }

    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportAccountsToExcel() throws Exception {
        List<Account> accounts = accountService.retrieveAccount();
        ByteArrayInputStream excelStream = accountService.exportAccountsToExcel(accounts);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "accounts.xlsx");
        byte[] excelBytes = excelStream.readAllBytes();
        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }

//    @GetMapping("/zakat-eligible")
//    public List<Account> getZakatEligibleAccounts() {
//        return accountService.getZakatEligibleAccounts();
//    }

    @PostMapping("/{id}/zakat-transaction")
    public Account addZakatTransaction(
            @PathVariable("id") Integer accountId,
            @RequestParam Double zakatAmount) {
        return accountService.addZakatTransaction(accountId, zakatAmount);
    }

    @GetMapping("/{id}/zakat-history-pdf")
    public ResponseEntity<byte[]> generateZakatHistoryPdf(@PathVariable("id") Integer idAccount) throws Exception {
        byte[] pdfBytes = pdfService.generateZakatHistoryPdf(idAccount);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "zakat_history_" + idAccount + ".pdf");
        headers.setContentLength(pdfBytes.length);
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

//    @GetMapping("/poor-accounts")
//    public List<Account> getPoorAccountsSorted(@RequestParam int year) {
//        return accountService.getPoorAccountsSorted(year);
//    }
//
//    @PostMapping("/distribute-zakat")
//    public ResponseEntity<String> distributeZakatToPoorAccounts(@RequestParam int year) {
//        accountService.distributeZakatToPoorAccounts(year);
//        return ResponseEntity.ok("Zakat distribuée avec succès pour l'année " + year);
//    }

    private Users extractUser(String token) {
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        return authService.getUserByEmail(email);
    }
    // Add this new endpoint
    @GetMapping("/by-rib/{rib}")
    public Account retrieveAccountByRib(@PathVariable String rib) {
        return accountService.retrieveAccountByRib(rib);
    }
    @PutMapping("/updateaccount/{rib}")
    public Account updateAccount(
            @PathVariable String rib,
            @Valid @RequestBody Account updatedAccount
    ) {
        return accountService.updateAccount(rib, updatedAccount);
    }
    @GetMapping("/poor-accounts")
    public ResponseEntity<List<Account>> getPoorAccountsSorted(@RequestParam int year) {
        try {
            logger.debug("Retrieving poor accounts for year: {}", year);
            List<Account> accounts = accountService.getPoorAccountsSorted(year);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            logger.error("Error retrieving poor accounts for year {}: {}", year, e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/distribute-zakat")
    public ResponseEntity<String> distributeZakatToPoorAccounts(@RequestParam int year) {
        try {
            logger.debug("Distributing Zakat for year: {}", year);
            accountService.distributeZakatToPoorAccounts(year);
            return ResponseEntity.ok("Zakat distribuée avec succès pour l'année " + year);
        } catch (Exception e) {
            logger.error("Error distributing Zakat for year {}: {}", year, e.getMessage(), e);
            return ResponseEntity.status(500).body("Error distributing Zakat: " + e.getMessage());
        }
    }

    @GetMapping("/by-rib/{rib}/zakat-status-pdf")
    public ResponseEntity<byte[]> generateZakatStatusPdf(
            @PathVariable("rib") String rib,
            @RequestParam("checkDate") String checkDate) {
        logger.info("Generating Zakat status PDF for RIB {} with checkDate {}", rib, checkDate);
        if (!rib.matches("TN\\d{20}")) {
            logger.error("Invalid RIB format: {}", rib);
            return ResponseEntity.status(400).body(("Invalid RIB format: " + rib).getBytes());
        }
        LocalDate date;
        try {
            date = LocalDate.parse(checkDate);
        } catch (DateTimeParseException e) {
            logger.error("Invalid checkDate format: {}", checkDate);
            return ResponseEntity.status(400).body(("Invalid checkDate format: " + checkDate).getBytes());
        }
        try {
            Account account = accountService.retrieveAccountByRib(rib);
            if (account.getAccountType() != AccountType.EPARGNE_ZEKET) {
                logger.error("Account with RIB {} is not a Zakat account", rib);
                return ResponseEntity.status(400).body(("Account with RIB " + rib + " is not a Zakat account").getBytes());
            }
            ByteArrayInputStream pdfStream = accountService.generateZakatStatusPDF(account.getId(), date);
            byte[] pdfBytes = pdfStream.readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "zakat_status_" + rib + ".pdf");
            headers.setContentLength(pdfBytes.length);
            logger.info("Successfully generated Zakat status PDF for RIB {}, size {} bytes", rib, pdfBytes.length);
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (RuntimeException e) {
            logger.error("Failed to generate Zakat status PDF for RIB {}: {}", rib, e.getMessage());
            return ResponseEntity.status(404).body(("Account with RIB " + rib + " not found").getBytes());
        } catch (Exception e) {
            logger.error("Unexpected error generating Zakat status PDF for RIB {}: {}", rib, e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }
}