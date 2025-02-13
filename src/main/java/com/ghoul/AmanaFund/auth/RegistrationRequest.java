package com.ghoul.AmanaFund.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Setter
@Builder
@Getter
@AllArgsConstructor
public class RegistrationRequest {
    @NotEmpty(message = "firstname not empty")
    @NotBlank(message = "firstname not empty")
    private String firstName;
    @NotEmpty(message = "lastName not empty")
    @NotBlank(message = "lastName not empty")
    private String lastName;
    @NotEmpty(message = "email not empty")
    @NotBlank(message = "email not empty")
    @Email
    private String email;
    @NotEmpty(message = "password not empty")
    @NotBlank(message = "password not empty")
    @Size(min = 8 , message = "minimum 8 characters")
    private String password;


}
