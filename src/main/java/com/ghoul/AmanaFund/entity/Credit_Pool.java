package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Credit_Pool {
    @Id
    @GeneratedValue
    private int id_credit_pool;
    private double minValue;
    private double maxValue;
    private int n_Echeance;
    private double pool_Sum;
    private boolean full;
    private LocalDateTime open_Date;
    private LocalDateTime close_Date;
    private Timestamp grace_Period;
    private Timestamp Period;
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "creditPool")
    private Set<Contract> contracts;

}
