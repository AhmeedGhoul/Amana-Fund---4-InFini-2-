package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(unique = true, nullable = false) // Contrainte au niveau de la base, mais pas dans la validation
    private String rib; // Plus de @NotBlank ici

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private Users user;
}