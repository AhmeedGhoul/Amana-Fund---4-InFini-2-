package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Entity
@Getter
@Builder
@AllArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue
    private int activityId;
    @NotBlank(message = "Activity name cannot be empty or null")
    private String activityName;

    @NotBlank(message = "Activity description cannot be empty or null")
    private String activityDescription;

    @NotNull(message = "Activity date is required")
    @PastOrPresent(message = "Activity date cannot be in the future")
    private LocalDateTime activityDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_id", nullable = true)
    private Audit audit;

    @Column(nullable = true)
    private String ipAddress;

    @Column(nullable = true)
    private String country;


    public ActivityLog(LocalDateTime activityDate, String activityDescription, String activityName) {
        this.activityDate = activityDate;

        this.activityDescription = activityDescription;
        this.activityName = activityName;
    }

    public ActivityLog(String activityName, String activityDescription, LocalDateTime activityDate, Users user,Audit audit) {
        this.activityName = activityName;
        this.activityDescription = activityDescription;

        this.activityDate = activityDate;
        this.user = user;
        this.audit = audit;
    }
    public ActivityLog(String activityName, String activityDescription, LocalDateTime activityDate, Users user,Audit audit, String ipAddress, String country) {
        this.activityName = activityName;
        this.activityDescription = activityDescription;

        this.activityDate = activityDate;
        this.user = user;
        this.audit = audit;
        this.ipAddress = ipAddress;
        this.country = country;
    }

    public ActivityLog() {

    }
}
