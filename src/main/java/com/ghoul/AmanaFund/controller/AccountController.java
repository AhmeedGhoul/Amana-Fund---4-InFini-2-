package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.entity.AccountType;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.security.JwtService;
import com.ghoul.AmanaFund.service.AuthenticationService;
import com.ghoul.AmanaFund.service.IAccountService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
    @RequestMapping("Account")
public class AccountController {
    IAccountService accountService;
    private final JwtService jwtService;
    private final AuthenticationService authService;

    @PostMapping("/addaccount")
    public Account ajouterAccount(@Valid @RequestBody Account account, @RequestHeader("Authorization") String token) {
        Users adminUser = extractUser(token);
        account.setUser(adminUser);
        return accountService.AddAccount(account);
    }
    private Users extractUser(String token) {
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        return authService.getUserByEmail(email);
    }
    @GetMapping("dispaccount")
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

    @PutMapping("updateaccount")
    public Account updateAccount(@Valid @RequestBody Account account) {
        return accountService.updateAccount(account);
    }

    @DeleteMapping("delaccount/{id}")
    public void removeAccount(@PathVariable("id") Integer idAccount) {
        accountService.removeAccount(idAccount);
    }

    @GetMapping("dispaccountId/{id}")
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
}