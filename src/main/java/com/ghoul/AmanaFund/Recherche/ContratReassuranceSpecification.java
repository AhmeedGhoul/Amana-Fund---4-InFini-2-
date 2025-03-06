package com.ghoul.AmanaFund.Recherche;

import com.ghoul.AmanaFund.entity.ContratReassurance;
import org.springframework.data.jpa.domain.Specification;

import java.util.Calendar;
import java.util.Date;

public class ContratReassuranceSpecification {
    public static Specification<ContratReassurance> hasId(Long idContrat) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("idContrat"), idContrat);
    }

    public static Specification<ContratReassurance> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<ContratReassurance> hasDate(Date date) {
        return (root, query, criteriaBuilder) -> {
            // Supprimer l'heure de la date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Date startDate = calendar.getTime();

            // Ajouter un jour pour définir une plage de comparaison
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date endDate = calendar.getTime();

            // Comparer la date dans la plage du jour
            return criteriaBuilder.between(root.get("date"), startDate, endDate);
        };
    }}
