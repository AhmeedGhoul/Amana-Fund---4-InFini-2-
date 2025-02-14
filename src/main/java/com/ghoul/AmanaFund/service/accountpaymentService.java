package com.ghoul.AmanaFund.service;


import com.ghoul.AmanaFund.entity.AccountPayment;
import com.ghoul.AmanaFund.repository.AccountPaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class accountpaymentService implements IAccountPaymentService {
    AccountPaymentRepository accountPaymentRepository;
    @Override
    public AccountPayment AddAccountPayment(AccountPayment accountPayment) {
        accountPaymentRepository.save(accountPayment);
        return accountPayment;
    }

    @Override
    public List<AccountPayment> retrieveAccountPayment() {
        return List.of();
    }

    @Override
    public AccountPayment updateAccountPayment(AccountPayment accountpayment) {
        return null;
    }

    @Override
    public AccountPayment retrieveAccountPayment(Integer idAccountPayment) {
        return null;
    }

    @Override
    public void removeAccountPayment(Integer idAccountPayment) {

    }
}
