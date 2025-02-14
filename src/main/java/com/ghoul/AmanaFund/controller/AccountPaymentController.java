package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.entity.AccountPayment;
import com.ghoul.AmanaFund.service.IAccountPaymentService;
import com.ghoul.AmanaFund.service.IAccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("AccountPayment")
public class AccountPaymentController {
    IAccountPaymentService accountPaymentService;
    @PostMapping("/addaccountPayment")
    public AccountPayment ajouterAccountPayment(@RequestBody AccountPayment accountpayment)
    {
        return accountPaymentService.AddAccountPayment(accountpayment);
    }


}
