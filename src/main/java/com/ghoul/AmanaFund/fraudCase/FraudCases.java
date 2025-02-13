package com.ghoul.AmanaFund.fraudCase;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FraudCases {
    @Id
    @GeneratedValue
    private int id_Fraud;
    private CaseType caseType;
    private LocalDateTime detectionDateTime;
    private CaseStatus caseStatus;
}
