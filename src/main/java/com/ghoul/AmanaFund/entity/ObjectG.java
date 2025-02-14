package com.ghoul.AmanaFund.entity;

import jakarta.persistence.Entity;
import lombok.*;

@Setter
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObjectG extends Garantie{

    private int Ownership_certif_number;
    private double Estimated_value;
    private TypeObject type;
}
