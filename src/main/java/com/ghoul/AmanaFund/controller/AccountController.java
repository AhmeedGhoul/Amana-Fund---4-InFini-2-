package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.service.accountService;
import com.ghoul.AmanaFund.service.IAccountService;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("Account")
public class AccountController {
    IAccountService accountService;
    @PostMapping("/addaccount")
    public Account ajouterAccount(@RequestBody Account account)
    {
        return accountService.AddAccount(account);
    }
    @GetMapping("dispaccount") // Ajout du mapping GET
    public List<Account> retrieveBlocs() {

        return accountService.retrieveAccount() ;
    }
    @PutMapping("updateaccount")
    public Account updateBloc(@RequestBody Account account) {
        return accountService.updateAccount(account);
    }
    @DeleteMapping("delaccount/{id}")
    public void removeBloc(@PathVariable("id") Integer idAccount) {
        accountService.removeAccount(idAccount);
    }
}
