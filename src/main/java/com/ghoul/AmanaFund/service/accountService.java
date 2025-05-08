package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.entity.AccountPayment;
import com.ghoul.AmanaFund.entity.AccountType;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.repository.AccountPaymentRepository;
import com.ghoul.AmanaFund.repository.AccountRepository;
import com.ghoul.AmanaFund.repository.UserRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class accountService implements IAccountService {
    private static final Logger logger = LoggerFactory.getLogger(accountService.class);
    private final AccountRepository accountRepository;
    private final AccountPaymentRepository accountPaymentRepository;
    private final UserRepository userRepository;

    private static final double NISSAB = 1000.0;
    private static final double ZAKAT_RATE = 0.025;
    private static final double MAX_AMOUNT = 5000.0;
    private static final double MAX_PAYMENTS = 2000.0;
    private static final long ONE_YEAR_IN_DAYS = 365;

    @Override
    public Account AddAccount(Account account) {
        logger.debug("Adding account for client email: {}", account.getClientEmail());
        Users client = userRepository.findByEmail(account.getClientEmail())
                .orElseThrow(() -> {
                    logger.error("Client email {} does not exist", account.getClientEmail());
                    return new IllegalArgumentException("Client email does not exist");
                });

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
                default:
                    account.setInterestRate(0.0);
            }
        }
        checkNissabStatus(account);
        Account savedAccount = accountRepository.save(account);
        logger.info("Account added successfully with RIB: {}", savedAccount.getRib());
        return savedAccount;
    }

    @Override
    public double calculateFutureValue(Integer accountId, LocalDate targetDate) {
        logger.debug("Calculating future value for account ID: {} on date: {}", accountId, targetDate);
        Account account = retrieveAccount(accountId);
        double initialAmount = account.getAmount() != null ? account.getAmount() : 0.0;
        double annualRate = account.getInterestRate() != null ? account.getInterestRate() : 0.0;
        LocalDate start = account.getDate_Opening() != null ? account.getDate_Opening().toLocalDate() : LocalDate.now();

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
                if (paymentDate != null && !paymentDate.isBefore(currentMonth) && paymentDate.isBefore(nextMonth)) {
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

        double result = Math.round(currentBalance * 100.0) / 100.0;
        logger.debug("Future value calculated: {}", result);
        return result;
    }

    @Override
    public double calculateInterestGained(Integer accountId, LocalDate targetDate) {
        logger.debug("Calculating interest gained for account ID: {} on date: {}", accountId, targetDate);
        double futureValue = calculateFutureValue(accountId, targetDate);
        Account account = retrieveAccount(accountId);
        double initialAmount = account.getAmount() != null ? account.getAmount() : 0.0;
        List<AccountPayment> payments = accountPaymentRepository.findByRib(account.getRib());
        double totalPayments = payments != null ? payments.stream().mapToDouble(AccountPayment::getAmount).sum() : 0.0;
        double interest = Math.round((futureValue - (initialAmount + totalPayments)) * 100.0) / 100.0;
        logger.debug("Interest gained: {}", interest);
        return interest;
    }

    @Override
    public ByteArrayInputStream exportAccountsToExcel(List<Account> accounts) throws IOException {
        logger.debug("Exporting {} accounts to Excel", accounts.size());
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Accounts");
            sheet.setDefaultColumnWidth(20);

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd hh:mm"));

            String[] columns = {
                    "ID", "Date d'ouverture", "Type de compte", "Montant", "RIB",
                    "Taux d'intérêt", "Email Client", "Agent", "Transactions Zakat",
                    "Dates Transactions Zakat", "Date Atteinte Nissab", "Éligible Zakat"
            };

            createHeaderRow(sheet, headerStyle, columns);

            int rowNum = 1;
            for (Account account : accounts) {
                Row row = sheet.createRow(rowNum++);

                createCell(row, 0, account.getId(), null);
                createCell(row, 1, account.getDate_Opening(), dateStyle);
                createCell(row, 2, account.getAccountType() != null ? account.getAccountType().toString() : "N/A", null);
                createCell(row, 3, account.getAmount() != null ? account.getAmount() : 0.0, null);
                createCell(row, 4, account.getRib(), null);
                createCell(row, 5, account.getInterestRate() != null ? account.getInterestRate() : 0.0, null);
                createCell(row, 6, account.getClientEmail(), null);
                createCell(row, 7, account.getAgent() != null ? account.getAgent().getUsername() : "N/A", null);
                createCell(row, 8, formatZakatTransactions(account.getZakatTransactions()), null);
                createCell(row, 9, formatZakatDates(account.getZakatTransactionDates()), null);
                createCell(row, 10, account.getNissabReachedDate(), dateStyle);
                createCell(row, 11, account.isEligibleForZakat() ? "Oui" : "Non", null);
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            logger.info("Excel export completed, size: {} bytes", out.size());
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

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

    private void createHeaderRow(Sheet sheet, CellStyle headerStyle, String[] columns) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }
    }

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

    private String formatZakatTransactions(List<Double> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return "Aucune";
        }
        return transactions.stream()
                .map(amount -> String.format("%.2f", amount))
                .collect(Collectors.joining(", "));
    }

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
        logger.debug("Retrieving all accounts");
        List<Account> accounts = accountRepository.findAll();
        logger.info("Retrieved {} accounts", accounts.size());
        return accounts;
    }

    @Override
    public Page<Account> retrieveAccount(Pageable pageable) {
        logger.debug("Retrieving accounts with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Account> accounts = accountRepository.findAll(pageable);
        logger.info("Retrieved {} accounts for page {}", accounts.getContent().size(), pageable.getPageNumber());
        return accounts;
    }

    @Override
    public Account updateAccount(Account account) {
        logger.debug("Updating account with ID: {}", account.getId());
        checkNissabStatus(account);
        Account updatedAccount = accountRepository.save(account);
        logger.info("Account updated successfully with ID: {}", updatedAccount.getId());
        return updatedAccount;
    }

    @Override
    public Account retrieveAccount(Integer idAccount) {
        logger.debug("Retrieving account with ID: {}", idAccount);
        Account account = accountRepository.findById(idAccount)
                .orElseThrow(() -> {
                    logger.error("Account not found with ID: {}", idAccount);
                    return new RuntimeException("Compte non trouvé");
                });
        logger.info("Account retrieved successfully with ID: {}", idAccount);
        return account;
    }

    @Override
    public void removeAccount(Integer idAccount) {
        logger.debug("Removing account with ID: {}", idAccount);
        accountRepository.deleteById(idAccount);
        logger.info("Account removed successfully with ID: {}", idAccount);
    }

    @Override
    public List<Account> findByAccountType(AccountType accountType) {
        logger.debug("Finding accounts by type: {}", accountType);
        List<Account> accounts = accountRepository.findByAccountType(accountType);
        logger.info("Found {} accounts of type {}", accounts.size(), accountType);
        return accounts;
    }

    @Override
    public List<Account> findByAmountGreaterThan(Double amount) {
        logger.debug("Finding accounts with amount greater than: {}", amount);
        List<Account> accounts = accountRepository.findByAmountGreaterThan(amount);
        logger.info("Found {} accounts with amount greater than {}", accounts.size(), amount);
        return accounts;
    }

    @Override
    public List<Account> getZakatEligibleAccounts() {
        logger.debug("Retrieving Zakat-eligible accounts");
        List<Account> accounts = accountRepository.findByAccountType(AccountType.EPARGNE_ZEKET)
                .stream()
                .filter(account -> account.isEligibleForZakat())
                .collect(Collectors.toList());
        logger.info("Found {} Zakat-eligible accounts", accounts.size());
        return accounts;
    }

    @Override
    public Account addZakatTransaction(Integer accountId, Double zakatAmount) {
        logger.debug("Adding Zakat transaction for account ID: {}, amount: {}", accountId, zakatAmount);
        Account account = retrieveAccount(accountId);
        if (account.getAccountType() == AccountType.EPARGNE_ZEKET && account.isEligibleForZakat()) {
            account.getZakatTransactions().add(zakatAmount);
            account.getZakatTransactionDates().add(LocalDateTime.now());
            account.setAmount(account.getAmount() != null ? account.getAmount() - zakatAmount : -zakatAmount);
            account.setNissabReachedDate(null);
            account.setEligibleForZakat(false);
            Account savedAccount = accountRepository.save(account);
            logger.info("Zakat transaction added for account ID: {}", accountId);
            return savedAccount;
        }
        logger.error("Account ID {} is not eligible for Zakat transaction", accountId);
        throw new RuntimeException("Compte non éligible pour une transaction Zakat");
    }

    @Override
    public void checkNissabStatus(Account account) {
        if (account == null || account.getAccountType() != AccountType.EPARGNE_ZEKET) {
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
        logger.debug("Retrieving poor accounts sorted for year: {}", year);
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

        List<Account> zakatAccounts = accountRepository.findByAccountType(AccountType.EPARGNE_ZEKET);
        Set<String> clientsReceivedZakat = zakatAccounts.stream()
                .filter(account -> account.getZakatTransactionDates() != null)
                .flatMap(account -> account.getZakatTransactionDates().stream()
                        .filter(date -> !date.toLocalDate().isBefore(startOfYear) && !date.toLocalDate().isAfter(endOfYear))
                        .map(date -> account.getClientEmail()))
                .collect(Collectors.toSet());

        List<Account> poorAccounts = accountRepository.findByAccountType(AccountType.EPARGNE)
                .stream()
                .filter(account -> !clientsReceivedZakat.contains(account.getClientEmail()))
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

        logger.info("Found {} poor accounts for year {}", poorAccounts.size(), year);
        return poorAccounts;
    }

    @Transactional
    @Override
    public void distributeZakatToPoorAccounts(int year) {
        logger.debug("Distributing Zakat for year: {}", year);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

        List<Account> poorAccounts = getPoorAccountsSorted(year);
        if (poorAccounts.isEmpty()) {
            logger.info("No poor accounts found for Zakat distribution in year {}", year);
            return;
        }

        List<Account> zakatAccounts = getZakatEligibleAccounts();
        if (zakatAccounts.isEmpty()) {
            logger.info("No Zakat-eligible accounts found for year {}", year);
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
        logger.info("Zakat distribution completed for year {}", year);
    }

    @Transactional
    protected void distributeZakatForAccount(Account zakatAccount, int year) {
        if (zakatAccount == null || zakatAccount.getAccountType() != AccountType.EPARGNE_ZEKET || !zakatAccount.isEligibleForZakat()) {
            logger.debug("Skipping Zakat distribution for invalid or ineligible account");
            return;
        }

        List<Account> poorAccounts = getPoorAccountsSorted(year);
        if (poorAccounts.isEmpty()) {
            logger.info("No poor accounts found for Zakat distribution");
            return;
        }

        Account poorestAccount = poorAccounts.get(0);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

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
        logger.info("Zakat distributed for account ID: {}", zakatAccount.getId());
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    @Override
    public void distributeZakatAutomatically() {
        int currentYear = LocalDate.now().getYear();
        logger.debug("Running automatic Zakat distribution for year: {}", currentYear);
        List<Account> zakatAccounts = accountRepository.findByAccountType(AccountType.EPARGNE_ZEKET);
        for (Account account : zakatAccounts) {
            if (account.getNissabReachedDate() != null) {
                LocalDate nissabDate = account.getNissabReachedDate().toLocalDate();
                long daysSinceNissab = ChronoUnit.DAYS.between(nissabDate, LocalDate.now());
                if (daysSinceNissab == ONE_YEAR_IN_DAYS && account.isEligibleForZakat()) {
                    distributeZakatForAccount(account, currentYear);
                }
            }
        }
        logger.info("Automatic Zakat distribution completed for year: {}", currentYear);
    }

    public String generateUniqueRib() {
        String rib;
        do {
            rib = "TN" + String.format("%018d", new Random().nextInt(999999999) + 1000000000);
        } while (accountRepository.findByRib(rib).isPresent());
        return rib;
    }

    @Override
    public Account retrieveAccountByRib(String rib) {
        logger.debug("Retrieving account by RIB: {}", rib);
        Account account = accountRepository.findByRib(rib)
                .orElseThrow(() -> {
                    logger.error("Account not found with RIB: {}", rib);
                    return new RuntimeException("Account not found with RIB: " + rib);
                });
        logger.info("Account retrieved successfully with RIB: {}", rib);
        return account;
    }

    @Override
    @Transactional
    public Account updateAccount(String rib, Account updatedAccount) {
        logger.debug("Updating account with RIB: {}", rib);
        Account existingAccount = accountRepository.findByRib(rib)
                .orElseThrow(() -> {
                    logger.error("Account not found with RIB: {}", rib);
                    return new RuntimeException("Account not found with RIB: " + rib);
                });

        Double oldAmount = existingAccount.getAmount() != null ? existingAccount.getAmount() : 0.0;
        Double newAmount = updatedAccount.getAmount() != null ? updatedAccount.getAmount() : 0.0;
        Double difference = newAmount - oldAmount;

        if (difference != 0.0) {
            AccountPayment payment = new AccountPayment();
            payment.setPaymentDate(LocalDate.now());
            payment.setAmount(difference);
            payment.setAgencyName("system");
            payment.setRib(existingAccount.getRib());
            accountPaymentRepository.save(payment);
        }

        existingAccount.setAmount(newAmount);
        existingAccount.setAccountType(updatedAccount.getAccountType());
        existingAccount.setClientEmail(updatedAccount.getClientEmail());

        checkNissabStatus(existingAccount);
        Account savedAccount = accountRepository.save(existingAccount);
        logger.info("Account updated successfully with RIB: {}", rib);
        return savedAccount;
    }

    @Override
    public ByteArrayInputStream generateZakatStatusPDF(Integer accountId, LocalDate checkDate) throws IOException {
        logger.info("Generating Zakat status PDF for account ID {} on date {}", accountId, checkDate);
        Account account = retrieveAccount(accountId);
        if (account.getAccountType() != AccountType.EPARGNE_ZEKET) {
            logger.error("Account ID {} is not a Zakat account", accountId);
            throw new IllegalArgumentException("Account is not a Zakat account");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            int year = checkDate.getYear();

            document.add(new Paragraph("Zakat Status Report")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Account Details")
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(20));
            document.add(new Paragraph("RIB: " + account.getRib()));
            document.add(new Paragraph("Client Email: " + account.getClientEmail()));
            document.add(new Paragraph(String.format("Current Amount: %.2f", account.getAmount() != null ? account.getAmount() : 0.0)));
            document.add(new Paragraph(String.format("Nissab Threshold (%d): %.2f", year, NISSAB)));

            document.add(new Paragraph("Zakat Status")
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(20));

            if (account.getNissabReachedDate() == null) {
                document.add(new Paragraph("Status: Account has not reached Nissab threshold."));
            } else {
                LocalDate nissabDate = account.getNissabReachedDate().toLocalDate();
                LocalDate oneYearLater = nissabDate.plusYears(1);
                long daysRemaining = ChronoUnit.DAYS.between(checkDate, oneYearLater);
                boolean isEligible = account.isEligibleForZakat();
                double zakatAmount = (account.getAmount() != null ? account.getAmount() : 0.0) * ZAKAT_RATE;

                if (isEligible && (checkDate.isAfter(oneYearLater) || checkDate.isEqual(oneYearLater))) {
                    List<Account> poorAccounts = getPoorAccountsSorted(year);
                    Account poorAccount = poorAccounts.isEmpty() ? null : poorAccounts.get(0);

                    document.add(new Paragraph("Status: Zakat has been distributed."));
                    document.add(new Paragraph("Nissab Reached Date: " + nissabDate));
                    document.add(new Paragraph("Zakat Distribution Date: " + oneYearLater));
                    document.add(new Paragraph(String.format("Zakat Amount: %.2f", zakatAmount)));
                    if (poorAccount != null) {
                        document.add(new Paragraph("Recipient Account:"));
                        document.add(new Paragraph("  Email: " + poorAccount.getClientEmail()));
                        document.add(new Paragraph("  RIB: " + poorAccount.getRib()));
                        document.add(new Paragraph(String.format("  Amount Received: %.2f", zakatAmount)));
                    }
                } else {
                    int monthsRemaining = (int) (daysRemaining / 30);
                    document.add(new Paragraph("Status: Awaiting Zakat distribution."));
                    document.add(new Paragraph("Nissab Reached Date: " + nissabDate));
                    document.add(new Paragraph("Days Remaining: " + daysRemaining + " (approximately " + monthsRemaining + " months)"));
                    document.add(new Paragraph(String.format("Estimated Zakat Amount: %.2f", zakatAmount)));
                }
            }

            document.add(new Paragraph("Zakat Calculation Steps")
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(20));
            document.add(new Paragraph("1. Nissab Verification: The account balance is checked against the Nissab threshold (" + String.format("%.2f", NISSAB) + " for " + year + ")."));
            document.add(new Paragraph("2. One-Year Monitoring: If the balance reaches Nissab, it is monitored for one year to ensure it remains above the threshold."));
            document.add(new Paragraph("3. Zakat Calculation: If eligible, Zakat is calculated as 2.5% of the account balance."));
            document.add(new Paragraph("4. Distribution: The Zakat amount is transferred to an eligible poor account, ensuring the recipient has not received Zakat in the current year."));
        }

        logger.info("Successfully generated Zakat status PDF for account ID {}, size {} bytes", accountId, out.size());
        return new ByteArrayInputStream(out.toByteArray());
    }
}