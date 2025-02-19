package com.ghoul.AmanaFund.Dao;

import com.ghoul.AmanaFund.entity.CivilStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Setter
@Builder
@Getter
@AllArgsConstructor

public class RegistrationRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(unique = true)
    private String email;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 150, message = "Age cannot be greater than 150")
    private int age;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Civil Status is required")
    private CivilStatus civilStatus;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    // Getters and setters (if needed)
}

