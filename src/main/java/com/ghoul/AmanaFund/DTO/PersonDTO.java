package com.ghoul.AmanaFund.DTO;

import com.ghoul.AmanaFund.entity.Person;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PersonDTO {
    private Long idGarantie;
    private String name;
    private String lastName;
    private String cin;
    private String email;
    private int age;
    private Double revenue;
    private boolean active;
    private String documents;
    private Long policeId;
}
