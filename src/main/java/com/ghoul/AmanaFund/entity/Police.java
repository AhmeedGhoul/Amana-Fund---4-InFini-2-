package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
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
    private Long idPolice;
    private Date start;
    private Date end;
    private Double amount;
    private Integer frequency;
    private Date renewalDate;
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "police")
    private Set<Garantie> Garanties;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
}
