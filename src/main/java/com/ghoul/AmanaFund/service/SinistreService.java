package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.Recherche.SinistreSpecification;
import com.ghoul.AmanaFund.entity.Sinistre;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.repository.SinistreRepository;
import com.ghoul.AmanaFund.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SinistreService implements  ISinistreService{
    private final SinistreRepository sinistreRepository;
    private final UserRepository userRepository;
    private final SMSsinistre smSsinistre;
    @Override
    public Sinistre add(Sinistre sinistre) {
        Sinistre savedSinistre = sinistreRepository.save(sinistre);

        // Appelle la méthode pour envoyer un SMS après l'ajout du sinistre
        sendMessage(savedSinistre);

        return savedSinistre;
    }

    @Override
    public Sinistre update(Sinistre sinistre) {
        return sinistreRepository.save(sinistre);
    }

    @Override
    public void remove(long id) {
        sinistreRepository.deleteById(id);

    }

    @Override
    public Sinistre getById(long id) {
        return sinistreRepository.findById(id).orElse(null);
    }

    @Override
    public List<Sinistre> getAll() {
        return sinistreRepository.findAll();
    }

    @Override
    public Page<Sinistre> getAllPaginated(Pageable pageable) {
        return sinistreRepository.findAll(pageable);
    }
    public List<Sinistre> searchSinistres(Long idSinistre, Double claimAmount, Date settlementDate, Double settlementAmount) {
        Specification<Sinistre> spec = Specification.where(null);

        if (idSinistre != null) {
            spec = spec.and(SinistreSpecification.hasId(idSinistre));
        }
        if (claimAmount != null) {
            spec = spec.and(SinistreSpecification.hasClaimAmountGreaterThan(claimAmount));
        }
        if (settlementDate != null) {
            spec = spec.and(SinistreSpecification.hasSettlementDateAfter(settlementDate));
        }
        if (settlementAmount != null) {
            spec = spec.and(SinistreSpecification.hasSettlementAmountLessThan(settlementAmount));
        }

        return sinistreRepository.findAll(spec);
    }
    public double calculerIndemnisationFinale(Long idSinistre) {
        Optional<Sinistre> optionalSinistre = sinistreRepository.findById(idSinistre);
        if (optionalSinistre.isEmpty()) {
            throw new RuntimeException("Sinistre non trouvé avec l'ID : " + idSinistre);
        }

        Sinistre sinistre = optionalSinistre.get();
        double partReassureur = sinistre.getClaimAmount() * sinistre.getReinsuranceShaire();
        double montantFinal = sinistre.getClaimAmount() - partReassureur;

        if (montantFinal <= 0) {
            throw new IllegalArgumentException("Erreur : Le montant de l'indemnisation est invalide.");
        }

        return montantFinal;
    }
    public double predireFondsDeReserve() {
        List<Sinistre> sinistres = sinistreRepository.findAll();
        if (sinistres.isEmpty()) {
            return 0.0;
        }

        double totalSinistres = sinistres.stream().mapToDouble(Sinistre::getClaimAmount).sum();
        return (totalSinistres / sinistres.size()) * 1.2; // 20% de marge de sécurité
    }
    public int evaluerRisque(Long userid) {
        Users user = userRepository.findById(userid)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer le nombre de sinistres de l'utilisateur
        long nombreSinistres = user.getNumberOfSinistres();

        // Calculer le montant total des sinistres
        double montantTotalSinistres = user.getSinistres().stream()
                .mapToDouble(Sinistre::getClaimAmount)
                .sum();

        // Évaluation du risque en fonction du nombre de sinistres et du montant total
        if (nombreSinistres == 0) {
            return 0; // Risque faible : aucun sinistre
        }
        if (nombreSinistres < 3 && montantTotalSinistres < 5000) {
            return 1; // Risque moyen
        }
        return 2; // Risque élevé
    }
    private void sendMessage(Sinistre sinistre) {
        // Remplace par le numéro de téléphone du destinataire
        String toPhoneNumber = "+21693107541"; // Exemple de numéro à adapter selon le besoin
        String messageBody = "Un nouveau sinistre a été ajouté. ID: " + sinistre.getIdSinistre() + ", Montant: " + sinistre.getClaimAmount();

        // Envoie le message
        smSsinistre.sendSms(toPhoneNumber, messageBody);
    }
// Ajout dans SinistreService

    public List<Sinistre> getSinistresByUserId(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer tous les sinistres associés à cet utilisateur
        return sinistreRepository.findByUser(user);
    }

}
