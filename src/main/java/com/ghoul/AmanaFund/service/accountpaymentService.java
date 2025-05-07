package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.dto.PaymentStatisticsDTO;
import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.entity.AccountPayment;
import com.ghoul.AmanaFund.repository.AccountPaymentRepository;
import com.ghoul.AmanaFund.repository.AccountRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
@Service
@AllArgsConstructor
@Slf4j
public class accountpaymentService implements IAccountPaymentService {
    private final AccountPaymentRepository accountPaymentRepository;
    private final AccountRepository accountRepository;
    private final IAccountService accountService;
    private final JavaMailSender mailSender;

    @Transactional
    @Override
    public AccountPayment AddAccountPayment(AccountPayment accountPayment) {
        String rib = accountPayment.getRib();
        if (rib != null && !rib.isBlank()) {
            Account account = accountRepository.findByRib(rib)
                    .orElseThrow(() -> new RuntimeException("Compte avec RIB " + rib + " non trouvé"));

            double newAmount = (account.getAmount() != null ? account.getAmount() : 0.0) + accountPayment.getAmount();
            account.setAmount(newAmount);
            accountService.checkNissabStatus(account);
            accountRepository.save(account);

            accountPayment.setRib(account.getRib());
            AccountPayment savedPayment = accountPaymentRepository.save(accountPayment);

            // Send payment confirmation email
            sendPaymentConfirmationEmail(account, savedPayment, newAmount);

            return savedPayment;
        } else {
            throw new RuntimeException("Aucun RIB associé au paiement");
        }
    }

    private void sendPaymentConfirmationEmail(Account account, AccountPayment payment, double newBalance) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(account.getClientEmail());
            message.setSubject("Payment Confirmation");
            message.setText(String.format(
                    "Dear Client,\n\n" +
                            "A new payment has been added to your account:\n\n" +
                            "Amount: %.2f TND\n" +
                            "Date: %s\n" +
                            "Agency: %s\n" +
                            "New Balance: %.2f TND\n\n" +
                            "Thank you for using our services.",
                    payment.getAmount(),
                    payment.getPaymentDate(),
                    payment.getAgencyName(),
                    newBalance
            ));

            mailSender.send(message);
            log.info("Payment confirmation email sent to {}", account.getClientEmail());
        } catch (Exception e) {
            log.error("Failed to send payment confirmation email", e);
        }
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
            accountService.checkNissabStatus(account);
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
            accountService.checkNissabStatus(account);
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

    @Override
    public List<AccountPayment> findByRib(String rib) {
        return accountPaymentRepository.findByRib(rib);
    }

    @Override
    public List<PaymentStatisticsDTO> getPaymentStatistics(String rib, String periodType) {
        List<AccountPayment> payments = accountPaymentRepository.findByRib(rib);

        return payments.stream()
                .filter(payment -> payment.getPaymentDate() != null)
                .collect(Collectors.groupingBy(
                        payment -> getPeriodKey(payment.getPaymentDate(), periodType),
                        Collectors.summingDouble(AccountPayment::getAmount)
                ))
                .entrySet().stream()
                .map(entry -> new PaymentStatisticsDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    // ... existing methods remain the same ...

    @Override
    public Page<AccountPayment> getPaymentsWithFilters(LocalDate startDate, LocalDate endDate, String rib, String agencyName, Pageable pageable) {
        if (startDate != null && endDate != null && rib != null && !rib.isEmpty() && agencyName != null && !agencyName.isEmpty()) {
            return accountPaymentRepository.findByPaymentDateBetweenAndRibAndAgencyName(startDate, endDate, rib, agencyName, pageable);
        } else if (startDate != null && endDate != null && rib != null && !rib.isEmpty()) {
            return accountPaymentRepository.findByPaymentDateBetweenAndRib(startDate, endDate, rib, pageable);
        } else if (startDate != null && endDate != null && agencyName != null && !agencyName.isEmpty()) {
            return accountPaymentRepository.findByPaymentDateBetweenAndAgencyName(startDate, endDate, agencyName, pageable);
        } else if (rib != null && !rib.isEmpty() && agencyName != null && !agencyName.isEmpty()) {
            return accountPaymentRepository.findByRibAndAgencyName(rib, agencyName, pageable);
        } else if (startDate != null && endDate != null) {
            return accountPaymentRepository.findByPaymentDateBetween(startDate, endDate, pageable);
        } else if (rib != null && !rib.isEmpty()) {
            return accountPaymentRepository.findByRib(rib, pageable);
        } else if (agencyName != null && !agencyName.isEmpty()) {
            return accountPaymentRepository.findByAgencyName(agencyName, pageable);
        } else {
            return accountPaymentRepository.findAll(pageable);
        }
    }

    @Override
    public Page<AccountPayment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return accountPaymentRepository.findByPaymentDateBetween(startDate, endDate, pageable);
    }

    @Override
    public Page<AccountPayment> findByRib(String rib, Pageable pageable) {
        return accountPaymentRepository.findByRib(rib, pageable);
    }

    @Override
    public Page<AccountPayment> findByAgencyName(String agencyName, Pageable pageable) {
        return accountPaymentRepository.findByAgencyName(agencyName, pageable);
    }

    private String getPeriodKey(LocalDate date, String periodType) {
        switch (periodType.toLowerCase()) {
            case "daily": return date.format(DateTimeFormatter.ISO_DATE);
            case "monthly": return YearMonth.from(date).toString();
            case "yearly": return String.valueOf(date.getYear());
            default: throw new IllegalArgumentException("Invalid period type");
        }
    }
    // Add these methods to your accountpaymentService class



}