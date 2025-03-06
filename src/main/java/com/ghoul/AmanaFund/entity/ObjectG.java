package com.ghoul.AmanaFund.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Setter
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObjectG extends Garantie{
    @Column(unique = true, nullable = false)
    @NotNull(message = "Ownership certificate number is required")
    private int Ownership_certif_number;
    @NotNull(message = "Estimated value is required")
    @Positive(message = "Estimated value must be positive")
    private double Estimated_value;
    @NotNull(message = "Type is required")
    private TypeObject type;
}
