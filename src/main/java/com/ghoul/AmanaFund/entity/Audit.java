package com.ghoul.AmanaFund.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
    private LocalDateTime dateAudit;
    private StatusAudit statusAudit;
    private String output;
    private LocalDateTime reviewedDate;
    private AuditType auditType;
    @OneToMany(mappedBy = "audit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ActivityLog> activityLogs;
    @OneToMany(mappedBy = "audit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FraudCases> fraudCases;

}
