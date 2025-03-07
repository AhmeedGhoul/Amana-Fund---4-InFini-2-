package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue
    private int id;

    @NotNull(message = "La date d'ouverture est obligatoire")
    @PastOrPresent(message = "La date d'ouverture ne peut pas être dans le futur")
    private LocalDateTime date_Opening;

    @NotNull(message = "Le type de compte est requis")
    private AccountType accountType;

    @Positive(message = "Le montant doit être positif")
    private Double amount;

    @Column(unique = true, nullable = false)
    private String rib;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private Users user;

    @ElementCollection
    @CollectionTable(name = "zakat_transactions", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "zakat_amount")
    private List<Double> zakatTransactions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "zakat_transactions_dates", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "zakat_date")
    private List<LocalDateTime> zakatTransactionDates = new ArrayList<>();

    private LocalDateTime nissabReachedDate;
    private boolean isEligibleForZakat;

    // Taux d'intérêt déjà présent dans votre logique
    private Double interestRate;
}