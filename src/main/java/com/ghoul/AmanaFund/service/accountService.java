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
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class accountService implements IAccountService {
    AccountRepository accountRepository;
    AccountPaymentRepository accountPaymentRepository;

    private static final double NISSAB = 1000.0;
    private static final double ZAKAT_RATE = 0.025; // 2.5% pour la Zakat
    private static final double MAX_AMOUNT = 5000.0;
    private static final double MAX_PAYMENTS = 2000.0;
    private static final long ONE_YEAR_IN_DAYS = 365;

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
        checkNissabStatus(account);
        accountRepository.save(account);
        return account;
    }

    @Override
    public double calculateFutureValue(Integer accountId, LocalDate targetDate) {
        Account account = retrieveAccount(accountId);
        double initialAmount = account.getAmount() != null ? account.getAmount() : 0.0;
        double annualRate = account.getInterestRate();
        LocalDate start = account.getDate_Opening().toLocalDate();

        List<AccountPayment> payments = accountPaymentRepository.findByRib(account.getRib());
        long totalMonths = ChronoUnit.MONTHS.between(start, targetDate);

        double currentBalance = initialAmount;
        double totalInterest = 0.0;
        double monthlyRate = annualRate / 12.0;

        LocalDate currentMonth = start;
        for (int i = 0; i < totalMonths; i++) {
            double interestForMonth = currentBalance * monthlyRate;
            totalInterest += interestForMonth;
            currentBalance += interestForMonth;

            LocalDate nextMonth = currentMonth.plusMonths(1);
            for (AccountPayment payment : payments) {
                LocalDate paymentDate = payment.getPaymentDate();
                if (!paymentDate.isBefore(currentMonth) && paymentDate.isBefore(nextMonth)) {
                    currentBalance += payment.getAmount();
                }
            }
            currentMonth = nextMonth;
        }

        long remainingDays = ChronoUnit.DAYS.between(currentMonth, targetDate);
        if (remainingDays > 0) {
            double partialMonthFraction = remainingDays / 30.0;
            double interestForPartialMonth = currentBalance * monthlyRate * partialMonthFraction;
            totalInterest += interestForPartialMonth;
            currentBalance += interestForPartialMonth;
        }

        return Math.round(currentBalance * 100.0) / 100.0;
    }

    @Override
    public double calculateInterestGained(Integer accountId, LocalDate targetDate) {
        double futureValue = calculateFutureValue(accountId, targetDate);
        Account account = retrieveAccount(accountId);
        double initialAmount = account.getAmount() != null ? account.getAmount() : 0.0;
        List<AccountPayment> payments = accountPaymentRepository.findByRib(account.getRib());
        double totalPayments = payments.stream().mapToDouble(AccountPayment::getAmount).sum();
        return Math.round((futureValue - (initialAmount + totalPayments)) * 100.0) / 100.0;
    }

    @Override
    public ByteArrayInputStream exportAccountsToExcel(List<Account> accounts) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Accounts");
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Date d'ouverture", "Type de compte", "Montant", "RIB", "Taux d'intérêt"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFont(workbook.createFont());
                cell.setCellStyle(headerStyle);
            }

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

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

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
        checkNissabStatus(account);
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

    @Override
    public List<Account> getZakatEligibleAccounts() {
        return accountRepository.findByAccountType(AccountType.EPARGNE_ZEKET)
                .stream()
                .filter(account -> account.isEligibleForZakat())
                .collect(Collectors.toList());
    }

    @Override
    public Account addZakatTransaction(Integer accountId, Double zakatAmount) {
        Account account = retrieveAccount(accountId);
        if (account.getAccountType() == AccountType.EPARGNE_ZEKET && account.isEligibleForZakat()) {
            account.getZakatTransactions().add(zakatAmount);
            account.getZakatTransactionDates().add(LocalDateTime.now());
            account.setAmount(account.getAmount() - zakatAmount);
            account.setNissabReachedDate(null);
            account.setEligibleForZakat(false);
            return accountRepository.save(account);
        }
        throw new RuntimeException("Compte non éligible pour une transaction Zakat");
    }

    @Override
    public void checkNissabStatus(Account account) {
        if (account.getAccountType() != AccountType.EPARGNE_ZEKET) {
            return;
        }

        double currentAmount = account.getAmount() != null ? account.getAmount() : 0.0;

        if (currentAmount >= NISSAB) {
            if (account.getNissabReachedDate() == null) {
                account.setNissabReachedDate(LocalDateTime.now());
                account.setEligibleForZakat(false);
            } else {
                long daysSinceNissab = ChronoUnit.DAYS.between(account.getNissabReachedDate(), LocalDateTime.now());
                if (daysSinceNissab >= ONE_YEAR_IN_DAYS) {
                    account.setEligibleForZakat(true);
                }
            }
        } else {
            account.setNissabReachedDate(null);
            account.setEligibleForZakat(false);
        }
    }

    @Override
    public List<Account> getPoorAccountsSorted(int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

        return accountRepository.findByAccountType(AccountType.EPARGNE)
                .stream()
                .filter(account -> {
                    double totalPayments = accountPaymentRepository.findByRibAndPaymentDateBetween(
                                    account.getRib(), startOfYear, endOfYear)
                            .stream()
                            .mapToDouble(AccountPayment::getAmount)
                            .sum();
                    double currentAmount = account.getAmount() != null ? account.getAmount() : 0.0;
                    return currentAmount < MAX_AMOUNT && totalPayments < MAX_PAYMENTS;
                })
                .sorted(Comparator
                        .comparingDouble(Account::getAmount)
                        .thenComparingDouble(account -> accountPaymentRepository
                                .findByRibAndPaymentDateBetween(account.getRib(), startOfYear, endOfYear)
                                .stream()
                                .mapToDouble(AccountPayment::getAmount)
                                .sum()))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void distributeZakatToPoorAccounts(int year) {
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

        List<Account> poorAccounts = getPoorAccountsSorted(year);
        if (poorAccounts.isEmpty()) {
            return;
        }

        List<Account> zakatAccounts = getZakatEligibleAccounts();
        if (zakatAccounts.isEmpty()) {
            return;
        }

        Account poorestAccount = poorAccounts.get(0);

        for (Account zakatAccount : zakatAccounts) {
            double currentAmount = zakatAccount.getAmount() != null ? zakatAccount.getAmount() : 0.0;
            double zakatDue = currentAmount * ZAKAT_RATE;
            double interest = calculateInterestGained(zakatAccount.getId(), endOfYear);

            double amountToDeduct;
            if (zakatDue > interest) {
                amountToDeduct = zakatDue;
                zakatAccount.setAmount(currentAmount - (zakatDue - interest));
            } else {
                amountToDeduct = zakatDue;
                zakatAccount.setAmount(currentAmount + (interest - zakatDue));
            }

            zakatAccount.getZakatTransactions().add(amountToDeduct);
            zakatAccount.getZakatTransactionDates().add(LocalDateTime.now());
            zakatAccount.setNissabReachedDate(null);
            zakatAccount.setEligibleForZakat(false);

            double poorCurrentAmount = poorestAccount.getAmount() != null ? poorestAccount.getAmount() : 0.0;
            poorestAccount.setAmount(poorCurrentAmount + amountToDeduct);

            accountRepository.save(zakatAccount);
            accountRepository.save(poorestAccount);
        }
    }

    private String generateUniqueRib() {
        String rib;
        do {
            rib = "TN" + String.format("%018d", new Random().nextInt(999999999) + 1000000000);
        } while (accountRepository.findByRib(rib).isPresent());
        return rib;
    }
}