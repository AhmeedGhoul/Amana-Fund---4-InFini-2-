package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Garantie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGarantie;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "police_id", nullable = false)
    private Police police;
}
