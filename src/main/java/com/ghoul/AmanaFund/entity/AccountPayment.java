package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Setter
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountPayment {
    @Id
    @GeneratedValue
    private int id;

    @NotNull(message = "La date de paiement est obligatoire")
    @PastOrPresent(message = "La date de paiement ne peut pas être dans le futur")
    private LocalDate paymentDate;

    @Positive(message = "Le montant doit être positif")
    private double amount;

    @NotBlank(message = "Le nom de l'agence est requis")
    private String agencyName;

    @NotBlank(message = "Le RIB est requis")
    @Column(name = "rib", nullable = false)
    private String rib;
}