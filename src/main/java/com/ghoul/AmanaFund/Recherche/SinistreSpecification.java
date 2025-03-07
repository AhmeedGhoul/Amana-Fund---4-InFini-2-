package com.ghoul.AmanaFund.Recherche;

import com.ghoul.AmanaFund.entity.Sinistre;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

import java.util.Calendar;
import java.util.Date;

public class SinistreSpecification {

    public static Specification<Sinistre> hasClaimAmountGreaterThan(Double amount) {
        return (root, query, criteriaBuilder) -> {
            if (amount == null) return null;
            return criteriaBuilder.greaterThanOrEqualTo(root.get("claimAmount"), amount);
        };
    }

    public static Specification<Sinistre> hasSettlementDateAfter(Date date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) return null;

            // Tronquer l'heure de la date pour ne comparer que la partie date (jour/mois/année)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Date startDate = calendar.getTime();

            // Comparer la date de règlement après ou égale à la date tronquée
            return criteriaBuilder.greaterThanOrEqualTo(root.get("settlementDate"), startDate);
        };
    }


    public static Specification<Sinistre> hasSettlementAmountLessThan(Double amount) {
        return (root, query, criteriaBuilder) -> {
            if (amount == null) return null;
            return criteriaBuilder.lessThanOrEqualTo(root.get("settlementAmount"), amount);
        };
    }
    public static Specification<Sinistre> hasId(Long id) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("idSinistre"), id);
    }
}
