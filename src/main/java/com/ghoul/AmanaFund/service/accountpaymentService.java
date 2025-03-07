package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.entity.AccountPayment;
import com.ghoul.AmanaFund.repository.AccountPaymentRepository;
import com.ghoul.AmanaFund.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class accountpaymentService implements IAccountPaymentService {
    AccountPaymentRepository accountPaymentRepository;
    AccountRepository accountRepository;
    IAccountService accountService; // Injection de IAccountService

    @Transactional
    @Override
    public AccountPayment AddAccountPayment(AccountPayment accountPayment) {
        String rib = accountPayment.getRib();
        if (rib != null && !rib.isBlank()) {
            Account account = accountRepository.findByRib(rib)
                    .orElseThrow(() -> new RuntimeException("Compte avec RIB " + rib + " non trouvé"));
            double newAmount = (account.getAmount() != null ? account.getAmount() : 0.0) + accountPayment.getAmount();
            account.setAmount(newAmount);
            accountService.checkNissabStatus(account); // Vérifier le statut Nissab après mise à jour
            accountRepository.save(account);
            accountPayment.setRib(account.getRib());
        } else {
            throw new RuntimeException("Aucun RIB associé au paiement");
        }
        return accountPaymentRepository.save(accountPayment);
    }

    @Override
    public List<AccountPayment> retrieveAccountPayment() {
        return accountPaymentRepository.findAll();
    }

    @Override
    public Page<AccountPayment> retrieveAccountPayment(Pageable pageable) {
        return accountPaymentRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public AccountPayment updateAccountPayment(AccountPayment accountPayment) {
        AccountPayment existingPayment = accountPaymentRepository.findById(accountPayment.getId())
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));

        String rib = accountPayment.getRib();
        if (rib != null && !rib.isBlank()) {
            Account account = accountRepository.findByRib(rib)
                    .orElseThrow(() -> new RuntimeException("Compte avec RIB " + rib + " non trouvé"));
            double oldAmount = existingPayment.getAmount();
            double newAmount = accountPayment.getAmount();
            double difference = newAmount - oldAmount;
            double updatedAccountAmount = (account.getAmount() != null ? account.getAmount() : 0.0) + difference;
            account.setAmount(updatedAccountAmount);
            accountService.checkNissabStatus(account); // Vérifier le statut Nissab après mise à jour
            accountRepository.save(account);
            accountPayment.setRib(account.getRib());
        } else {
            throw new RuntimeException("Aucun RIB associé au paiement");
        }
        return accountPaymentRepository.save(accountPayment);
    }

    @Transactional
    @Override
    public void removeAccountPayment(Integer idAccountPayment) {
        AccountPayment payment = accountPaymentRepository.findById(idAccountPayment)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));
        String rib = payment.getRib();
        if (rib != null) {
            Account account = accountRepository.findByRib(rib)
                    .orElseThrow(() -> new RuntimeException("Compte avec RIB " + rib + " non trouvé"));
            double updatedAmount = (account.getAmount() != null ? account.getAmount() : 0.0) - payment.getAmount();
            account.setAmount(updatedAmount);
            accountService.checkNissabStatus(account); // Vérifier le statut Nissab après mise à jour
            accountRepository.save(account);
        }
        accountPaymentRepository.deleteById(idAccountPayment);
    }

    @Override
    public AccountPayment retrieveAccountPayment(Integer idAccountPayment) {
        return accountPaymentRepository.findById(idAccountPayment).get();
    }

    @Override
    public List<AccountPayment> findByAgencyName(String agencyName) {
        return accountPaymentRepository.findByAgencyName(agencyName);
    }

    @Override
    public List<AccountPayment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate) {
        return accountPaymentRepository.findByPaymentDateBetween(startDate, endDate);
    }
}