package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_request;

    @NotNull(message = "Request date is required")
    @PastOrPresent(message = "Request date must be today or in the past")
    private LocalDate date_Request;

    @NotNull(message = "Product is required")
    private Product product;

    @NotBlank(message = "Document field cannot be empty")
    @Size(min = 5, max = 255, message = "Documents description must be between 5 and 255 characters")
    private String Document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
}
