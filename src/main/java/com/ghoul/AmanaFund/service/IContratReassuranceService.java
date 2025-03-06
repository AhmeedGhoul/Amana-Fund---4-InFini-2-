package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.ContratReassurance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface IContratReassuranceService {
    ContratReassurance add(ContratReassurance contratReassurance);
    ContratReassurance update(ContratReassurance contratReassurance);
    void remove(long id);
    ContratReassurance getById(long id);
    List<ContratReassurance> getAll();
    Page<ContratReassurance> getAllPaginated(Pageable pageable);
    List<ContratReassurance> searchContrats(Long idContrat, String name, Date date);

    double calculerRatioRentabilite(Long id);
}
