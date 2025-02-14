package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Agency {
    @Id
    @GeneratedValue
    private int id_agency;
    private Governorate governorate;
    private String adress;
    private String city;
    private int phoneNumber;
    private String email;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
}
