package com.ghoul.AmanaFund.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratReassurance {

    @Id
    @GeneratedValue
    private Long idContrat;

    @NotNull(message = "La date du contrat est obligatoire")
    @PastOrPresent(message = "La date du contrat ne peut pas être dans le futur")
    private Date date;

    @NotBlank(message = "Le nom du contrat est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String name;

    @NotBlank(message = "Le contact est obligatoire")
    @Size(min = 10, max = 50, message = "Le contact doit contenir entre 10 et 50 caractères")
    private String contact;

    @NotNull(message = "Le plafond de couverture est obligatoire")
    @Positive(message = "Le plafond de couverture doit être un montant positif")
    private Double coverageLimit;

    @NotNull(message = "La prime est obligatoire")
    @Positive(message = "La prime doit être un montant positif")
    private Double premium;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sinistre_id", nullable = false)
    @JsonIgnore
    private Sinistre sinistre;
}
