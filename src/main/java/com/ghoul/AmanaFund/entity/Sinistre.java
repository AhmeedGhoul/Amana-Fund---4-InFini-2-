package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sinistre {
    @Id
    @GeneratedValue
    private Long idSinistre;


    @NotNull(message = "Le montant de la réclamation est obligatoire")
    @Positive(message = "Le montant de la réclamation doit être positif")
    private Double claimAmount;

    @NotNull(message = "La part de la réassurance est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "La part de la réassurance ne peut pas être négative")
    @DecimalMax(value = "1.0", inclusive = true, message = "La part de la réassurance ne peut pas dépasser 1 (100%)")
    private Double reinsuranceShaire;

    @FutureOrPresent(message = "La date de règlement ne peut pas être dans le passé")
    private Date settlementDate;

    @NotNull(message = "Le montant du règlement est obligatoire")
    @Positive(message = "Le montant du règlement doit être positif")
    private Double settlementAmount;

    @OneToMany(mappedBy = "sinistre", cascade = CascadeType.ALL)
    private Set<ContratReassurance> contratReassurances;

    @OneToOne(cascade = CascadeType.ALL)

    private Police police;
}
