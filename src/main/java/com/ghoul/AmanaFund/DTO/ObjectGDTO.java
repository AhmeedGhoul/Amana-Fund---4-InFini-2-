package com.ghoul.AmanaFund.DTO;

import com.ghoul.AmanaFund.entity.TypeObject;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ObjectGDTO {
    private Long idGarantie;
    private boolean active;
    private String documents;
    private int ownershipCertifNumber;
    private double estimatedValue;
    private TypeObject type;
    private Long policeId;
}
