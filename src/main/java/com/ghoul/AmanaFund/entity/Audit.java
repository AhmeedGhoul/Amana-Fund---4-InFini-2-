package com.ghoul.AmanaFund.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Audit {
    @Id
    @GeneratedValue
    private int idAudit;
    @NotNull(message = "Audit date is required")
    @PastOrPresent(message = "Audit date cannot be in the future")
    private LocalDateTime dateAudit;

    @NotNull(message = "Audit status is required")
    private StatusAudit statusAudit;

    @Size(min = 3, message = "Output must be at least 3 characters long")
    private String output;

    @PastOrPresent(message = "Reviewed date cannot be in the future")
    private LocalDateTime reviewedDate;

    @NotNull(message = "Audit type is required")
    private AuditType auditType;
    @OneToMany(mappedBy = "audit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityLog> activityLogs;
    @OneToMany(mappedBy = "audit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FraudCases> fraudCases;
    @Override
    public String toString() {
        return ""+idAudit;
    }

}
