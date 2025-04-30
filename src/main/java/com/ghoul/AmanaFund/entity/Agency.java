package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Setter
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Agency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_agency;

    @NotNull(message = "Governorate is required")
    private Governorate governorate;

    @NotBlank(message = "Address cannot be empty")
    @Size(min = 5, max = 100, message = "Address must be between 5 and 100 characters")
    private String address;

    @NotBlank(message = "City cannot be empty")
    @Size(min = 3, max = 50, message = "City name must be between 3 and 50 characters")
    private String city;

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^\\d{8}$", message = "Phone number must be exactly 8 digits")
    private String phoneNumber;

    @NotBlank(message = "Email cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@amana\\.tn$", message = "Email must belong to the domain 'amana.tn'")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

}