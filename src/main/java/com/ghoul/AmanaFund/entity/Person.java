package com.ghoul.AmanaFund.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Email;
import lombok.experimental.SuperBuilder;
import lombok.*;

@Setter
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

public class Person extends Garantie{

    @Pattern(regexp = "\\d{8}", message = "CIN must be exactly 8 digits")
    private String CIN;

    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Name must contain only letters")
    private String name;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain only letters")
    private String last_name;

    @Positive(message = "Age must be a positive number")
    private int age;

    @NotNull(message = "Revenue is required")
    @Positive(message = "Revenue must be a positive number")
    private Double revenue;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

}
