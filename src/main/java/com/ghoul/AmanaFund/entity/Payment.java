package com.ghoul.AmanaFund.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue
    private int id_payment;
    private LocalDateTime date_payment;
    private String Agent;
    private Double amount;
    private Boolean Status ;

    @ManyToOne
    private Contract contract;
}
