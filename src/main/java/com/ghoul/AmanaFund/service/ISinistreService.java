package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.ContratReassurance;
import com.ghoul.AmanaFund.entity.Sinistre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface ISinistreService {
    Sinistre add(Sinistre sinistre);
    Sinistre update(Sinistre sinistre);
    void remove(long id);
   Sinistre getById(long id);
    List<Sinistre> getAll();
    Page<Sinistre> getAllPaginated(Pageable pageable);
    List<Sinistre> searchSinistres(Long idSinistre,Double claimAmount, Date settlementDate, Double settlementAmount);

    double calculerIndemnisationFinale(Long id);

    double predireFondsDeReserve();

    int evaluerRisque(Long userId);
}
