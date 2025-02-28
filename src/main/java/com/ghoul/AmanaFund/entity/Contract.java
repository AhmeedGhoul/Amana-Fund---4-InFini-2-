package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contract {
    @Id
    @GeneratedValue
    private int id_Contract;
    private LocalDateTime date_Contract;
    private String documents;
    private LocalDateTime withdrawal_date;
    private int queue_Number;
    private Double amount;
    private Double payed;
    @ManyToOne
    private Credit_Pool creditPool;
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "contract")
    private Set<Payment> Payments;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
}
