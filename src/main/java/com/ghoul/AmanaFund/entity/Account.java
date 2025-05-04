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

    @Min(value = 0, message = "Le montant doit être positif ou zéro")
    private Double amount;

    @Column(unique = true, nullable = false)
    private String rib;


    @NotBlank(message = "Client email is required")
    @Email(message = "Invalid client email format")
    private String clientEmail; // Client's email

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private Users agent; // Agent who created the account (renamed from 'user')

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