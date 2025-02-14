package com.ghoul.AmanaFund.entity;

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
public class Audit {
    @Id
    @GeneratedValue
    private int idAudit;
    private LocalDateTime dateAudit;
    private StatusAudit statusAudit;
    private String output;
    private LocalDateTime reviewedDate;
    private AuditType auditType;


}
