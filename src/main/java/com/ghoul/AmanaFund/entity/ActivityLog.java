package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
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
    private String activityName;
    private String activityDescription;
    private String oldValue;
    private String newValue;
    private LocalDateTime activityDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    public ActivityLog(LocalDateTime activityDate, String newValue, String oldValue, String activityDescription, String activityName) {
        this.activityDate = activityDate;
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.activityDescription = activityDescription;
        this.activityName = activityName;
    }

    public ActivityLog(String activityName, String activityDescription, String oldValue, String newValue, LocalDateTime activityDate, Users user) {
        this.activityName = activityName;
        this.activityDescription = activityDescription;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.activityDate = activityDate;
        this.user = user;
    }

    public ActivityLog() {

    }
}
