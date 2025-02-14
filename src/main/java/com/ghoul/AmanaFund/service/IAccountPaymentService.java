package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.entity.AccountPayment;

import java.util.List;

public interface IAccountPaymentService {
    AccountPayment AddAccountPayment(AccountPayment accountPayment);
    List<AccountPayment> retrieveAccountPayment();
    AccountPayment updateAccountPayment (AccountPayment accountpayment);
    AccountPayment retrieveAccountPayment (Integer idAccountPayment);
    void removeAccountPayment (Integer idAccountPayment);
}
