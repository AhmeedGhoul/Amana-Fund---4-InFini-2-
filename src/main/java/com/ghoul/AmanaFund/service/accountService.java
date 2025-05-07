package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.entity.AccountPayment;
import com.ghoul.AmanaFund.entity.AccountType;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.repository.AccountPaymentRepository;
import com.ghoul.AmanaFund.repository.AccountRepository;
import com.ghoul.AmanaFund.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    UserRepository userRepository; // Add UserRepository dependency

    private static final double NISSAB = 1000.0;
    private static final double ZAKAT_RATE = 0.025; // 2.5% pour la Zakat
    private static final double MAX_AMOUNT = 5000.0;
    private static final double MAX_PAYMENTS = 2000.0;
    private static final long ONE_YEAR_IN_DAYS = 365;

    @Override
    public Account AddAccount(Account account) {
        // Validate client's email exists
        Users client = userRepository.findByEmail(account.getClientEmail())
                .orElseThrow(() -> new IllegalArgumentException("Client email does not exist"));

        // Agent is already set in the controller via account.setAgent()
        // Generate RIB and set interest rate
        if (account.getRib() == null || account.getRib().isBlank()) {
            account.setRib(generateUniqueRib());
        }
        if (account.getInterestRate() == null) {
            switch (account.getAccountType()) {
                case EPARGNE:
                    account.setInterestRate(0.0799);
                    break;
                case EPARGNE_ZEKET:
                    account.setInterestRate(0.05);
                    break;
            }
        }
        checkNissabStatus(account);
        return accountRepository.save(account);
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
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Create sheet and set basic properties
            Sheet sheet = workbook.createSheet("Accounts");
            sheet.setDefaultColumnWidth(20);

            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);

            // Create data style for dates
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd hh:mm"));

            // Define all columns including new fields
            String[] columns = {
                    "ID",
                    "Date d'ouverture",
                    "Type de compte",
                    "Montant",
                    "RIB",
                    "Taux d'intérêt",
                    "Email Client",
                    "Agent",
                    "Transactions Zakat",
                    "Dates Transactions Zakat",
                    "Date Atteinte Nissab",
                    "Éligible Zakat"
            };

            // Create header row
            createHeaderRow(sheet, headerStyle, columns);

            // Populate data rows
            int rowNum = 1;
            for (Account account : accounts) {
                Row row = sheet.createRow(rowNum++);

                // Basic account info
                createCell(row, 0, account.getId(), null);
                createCell(row, 1, account.getDate_Opening(), dateStyle);
                createCell(row, 2, account.getAccountType().toString(), null);
                createCell(row, 3, account.getAmount() != null ? account.getAmount() : 0.0, null);
                createCell(row, 4, account.getRib(), null);
                createCell(row, 5, account.getInterestRate() != null ? account.getInterestRate() : 0.0, null);

                // New fields
                createCell(row, 6, account.getClientEmail(), null);
                createCell(row, 7, account.getAgent() != null ? account.getAgent().getUsername() : "N/A", null);

                // Zakat transactions info
                createCell(row, 8, formatZakatTransactions(account.getZakatTransactions()), null);
                createCell(row, 9, formatZakatDates(account.getZakatTransactionDates()), null);

                // Nissab info
                createCell(row, 10, account.getNissabReachedDate(), dateStyle);
                createCell(row, 11, account.isEligibleForZakat() ? "Oui" : "Non", null);
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // Helper method to create header style
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        return headerStyle;
    }

    // Helper method to create header row
    private void createHeaderRow(Sheet sheet, CellStyle headerStyle, String[] columns) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    // Helper method to create cells with optional style
    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);

        if (value == null) {
            cell.setCellValue("N/A");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
            if (style != null) {
                cell.setCellStyle(style);
            }
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value ? "Oui" : "Non");
        } else {
            cell.setCellValue(value.toString());
        }
    }

    // Helper method to format zakat transactions
    private String formatZakatTransactions(List<Double> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return "Aucune";
        }
        return transactions.stream()
                .map(amount -> String.format("%.2f", amount))
                .collect(Collectors.joining(", "));
    }

    // Helper method to format zakat dates
    private String formatZakatDates(List<LocalDateTime> dates) {
        if (dates == null || dates.isEmpty()) {
            return "Aucune";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dates.stream()
                .map(formatter::format)
                .collect(Collectors.joining(", "));
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
    @Override
    public Account retrieveAccountByRib(String rib) {
        return accountRepository.findByRib(rib)
                .orElseThrow(() -> new RuntimeException("Account not found with RIB: " + rib));
    }

    @Override
    @Transactional
    public Account updateAccount(String rib, Account updatedAccount) {
        // Fetch the existing account by RIB
        Account existingAccount = accountRepository.findByRib(rib)
                .orElseThrow(() -> new RuntimeException("Account not found with RIB: " + rib));

        // Capture old and new amounts
        Double oldAmount = existingAccount.getAmount() != null ? existingAccount.getAmount() : 0.0;
        Double newAmount = updatedAccount.getAmount() != null ? updatedAccount.getAmount() : 0.0;
        Double difference = newAmount - oldAmount;

        // Create payment if there's a change
        if (difference != 0.0) {
            AccountPayment payment = new AccountPayment();
            payment.setPaymentDate(LocalDate.now());
            payment.setAmount(difference);
            payment.setAgencyName("system");
            payment.setRib(existingAccount.getRib()); // Use the existing RIB
            accountPaymentRepository.save(payment);
        }

        // Update fields from the incoming updatedAccount (except immutable fields like RIB)
        existingAccount.setAmount(newAmount);
        existingAccount.setAccountType(updatedAccount.getAccountType());
        existingAccount.setClientEmail(updatedAccount.getClientEmail());
        // Update other fields as needed (e.g., interestRate, agent, etc.)

        // Check Nissab status and save
        checkNissabStatus(existingAccount);
        return accountRepository.save(existingAccount);
    }
}