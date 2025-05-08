package com.ghoul.AmanaFund.DTO;

import com.ghoul.AmanaFund.entity.CivilStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private int age;
    private String address;
    private CivilStatus civilStatus;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Boolean enabled;
    private Boolean accountDeleted;
    private Boolean accountLocked;
    private float userScore;
    private LocalDate createdDate;
    private long numberOfSinistres;
}
