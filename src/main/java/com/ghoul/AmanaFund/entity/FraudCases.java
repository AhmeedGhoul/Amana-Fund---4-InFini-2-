package com.ghoul.AmanaFund.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
   @NotNull(message = "Case type is required")
   private CaseType caseType;

    @NotNull(message = "Detection date is required")
    @PastOrPresent(message = "Detection date cannot be in the future")
    private LocalDateTime detectionDateTime;

    @NotNull(message = "Case status is required")
    private CaseStatus caseStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Audit_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "activityLogs", "fraudCases"})

    private Audit audit;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users responsibleUser;
}
