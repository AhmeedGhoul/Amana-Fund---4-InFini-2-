package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
    private LocalDate paymentDate;
    private double amount;
    private String AgencyName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountPayments", nullable = false)
    private Account account;
}
