package com.ghoul.AmanaFund.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
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
    private int ownershipCertifNumber;
    @NotNull(message = "Estimated value is required")
    @Positive(message = "Estimated value must be positive")
    private double estimatedValue;
    @NotNull(message = "Type is required")
    private TypeObject type;
}
