package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
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
    private Double claimAmount;
    private Double reinsuranceShaire;
    private Date settlementDate;
    private Double settlementAmount;
    @OneToMany(mappedBy = "sinistre", cascade = CascadeType.ALL)
    private Set<ContratReassurance> contratReassurances;
    @OneToOne(cascade = CascadeType.ALL)
    Police police;

}
