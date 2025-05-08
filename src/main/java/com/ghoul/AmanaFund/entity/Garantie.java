package com.ghoul.AmanaFund.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "idGarantie"
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    @NotNull(message = "Status should have value")
    private boolean Active = true;

    @NotBlank(message = "Documents are required")
    private String Documents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "police_id", nullable = false)
    private Police police;
}
