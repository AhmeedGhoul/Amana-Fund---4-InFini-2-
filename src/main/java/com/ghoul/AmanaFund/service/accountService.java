package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.entity.AccountPayment;
import com.ghoul.AmanaFund.entity.AccountType;
import com.ghoul.AmanaFund.repository.AccountPaymentRepository;
import com.ghoul.AmanaFund.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class accountService implements IAccountService {
    AccountRepository accountRepository;
    AccountPaymentRepository accountPaymentRepository;

    @Override
    public Account AddAccount(Account account) {
        if (account.getRib() == null || account.getRib().isBlank()) {
            account.setRib(generateUniqueRib());
        }
        if (account.getInterestRate() == null) {
            switch (account.getAccountType()) {
                case EPARGNE:
                    account.setInterestRate(0.015); // 1,5% par an
                    break;
                case EPARGNE_ZEKET:
                    account.setInterestRate(0.025); // 2,5% par an
                    break;
            }
        }
        accountRepository.save(account);
        return account;
    }

    @Override
    public double calculateFutureValue(Integer accountId, LocalDate targetDate) {
        Account account = retrieveAccount(accountId);
        double initialAmount = account.getAmount() != null ? account.getAmount() : 0.0;
        double annualRate = account.getInterestRate();
        LocalDate start = account.getDate_Opening().toLocalDate(); // Conversion de LocalDateTime à LocalDate

        // Récupérer tous les paiements associés au compte
        List<AccountPayment> payments = accountPaymentRepository.findByRib(account.getRib());

        // Calculer le nombre total de mois entre l’ouverture et la date cible
        long totalMonths = ChronoUnit.MONTHS.between(start, targetDate);

        // Initialiser le solde avec le montant initial
        double currentBalance = initialAmount;
        double totalInterest = 0.0;

        // Taux mensuel = taux annuel / 12
        double monthlyRate = annualRate / 12.0;

        // Simuler mois par mois
        LocalDate currentMonth = start;
        for (int i = 0; i < totalMonths; i++) {
            // Appliquer les intérêts sur le solde actuel
            double interestForMonth = currentBalance * monthlyRate;
            totalInterest += interestForMonth;
            currentBalance += interestForMonth;

            // Ajouter les paiements effectués ce mois-ci
            LocalDate nextMonth = currentMonth.plusMonths(1);
            for (AccountPayment payment : payments) {
                LocalDate paymentDate = payment.getPaymentDate();
                if (!paymentDate.isBefore(currentMonth) && paymentDate.isBefore(nextMonth)) {
                    currentBalance += payment.getAmount();
                }
            }
            currentMonth = nextMonth;
        }

        // Appliquer les intérêts pour le dernier mois partiel (si applicable)
        long remainingDays = ChronoUnit.DAYS.between(currentMonth, targetDate);
        if (remainingDays > 0) {
            double partialMonthFraction = remainingDays / 30.0; // Approximation 30 jours/mois
            double interestForPartialMonth = currentBalance * monthlyRate * partialMonthFraction;
            totalInterest += interestForPartialMonth;
            currentBalance += interestForPartialMonth;
        }

        return Math.round(currentBalance * 100.0) / 100.0; // Arrondi à 2 décimales
    }

    @Override
    public double calculateInterestGained(Integer accountId, LocalDate targetDate) {
        double futureValue = calculateFutureValue(accountId, targetDate);
        Account account = retrieveAccount(accountId);
        double initialAmount = account.getAmount() != null ? account.getAmount() : 0.0;
        List<AccountPayment> payments = accountPaymentRepository.findByRib(account.getRib());
        double totalPayments = payments.stream().mapToDouble(AccountPayment::getAmount).sum();
        return Math.round((futureValue - (initialAmount + totalPayments)) * 100.0) / 100.0; // Arrondi à 2 décimales
    }

    @Override
    public ByteArrayInputStream exportAccountsToExcel(List<Account> accounts) throws IOException {
        // Créer un nouveau classeur Excel
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Accounts");

            // Créer l'en-tête
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Date d'ouverture", "Type de compte", "Montant", "RIB", "Taux d'intérêt"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFont(workbook.createFont());
                cell.setCellStyle(headerStyle);
            }

            // Remplir les données
            int rowNum = 1;
            for (Account account : accounts) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(account.getId());
                row.createCell(1).setCellValue(account.getDate_Opening().toString());
                row.createCell(2).setCellValue(account.getAccountType().toString());
                row.createCell(3).setCellValue(account.getAmount() != null ? account.getAmount() : 0.0);
                row.createCell(4).setCellValue(account.getRib());
                row.createCell(5).setCellValue(account.getInterestRate() != null ? account.getInterestRate() : 0.0);
            }

            // Ajuster la taille des colonnes
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Écrire dans le flux de sortie
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    @Override
    public List<Account> retrieveAccount() {
        return accountRepository.findAll();
    }

    @Override
    public Page<Account> retrieveAccount(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Override
    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Account retrieveAccount(Integer idAccount) {
        return accountRepository.findById(idAccount).orElseThrow(() -> new RuntimeException("Compte non trouvé"));
    }

    @Override
    public void removeAccount(Integer idAccount) {
        accountRepository.deleteById(idAccount);
    }

    @Override
    public List<Account> findByAccountType(AccountType accountType) {
        return accountRepository.findByAccountType(accountType);
    }

    @Override
    public List<Account> findByAmountGreaterThan(Double amount) {
        return accountRepository.findByAmountGreaterThan(amount);
    }

    private String generateUniqueRib() {
        String rib;
        do {
            rib = "TN" + String.format("%018d", new Random().nextInt(999999999) + 1000000000);
        } while (accountRepository.findByRib(rib).isPresent());
        return rib;
    }
}