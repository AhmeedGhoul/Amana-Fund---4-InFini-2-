package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.engine.internal.Cascade;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Police {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPolice;
    private Date start;
    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    @Temporal(TemporalType.DATE)
    private Date end;
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    @NotNull(message = "Frequency is required")
    private FrequencyPolice frequency;
    @NotNull(message = "Renewal date is required")
    @Future(message = "Renewal date must be in the future")
    @Temporal(TemporalType.DATE)
    private Date renewalDate;
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "police")
    private Set<Garantie> Garanties;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
}
