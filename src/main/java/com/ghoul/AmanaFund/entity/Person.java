package com.ghoul.AmanaFund.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Setter
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person extends Garantie{

    private int CIN;
    private String name;
    private String last_name;
    private int age;
    private Double revenue;

    private String Documents;
}
