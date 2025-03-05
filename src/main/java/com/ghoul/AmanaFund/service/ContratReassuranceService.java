package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.Recherche.ContratReassuranceSpecification;
import com.ghoul.AmanaFund.entity.ContratReassurance;
import com.ghoul.AmanaFund.repository.ContratReassuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContratReassuranceService implements IContratReassuranceService {
    @Autowired
    private ContratReassuranceRepository contratReassuranceRepository;
    @Override
    public ContratReassurance add(ContratReassurance contratReassurance) {
        return contratReassuranceRepository.save(contratReassurance);
    }

    @Override
    public ContratReassurance update(ContratReassurance contratReassurance) {
        return contratReassuranceRepository.save(contratReassurance);
    }

    @Override
    public void remove(long id) {
        contratReassuranceRepository.deleteById(id);

    }

    @Override
    public ContratReassurance getById(long id) {
        return contratReassuranceRepository.findById(id).orElse(null);
    }

    @Override
    public List<ContratReassurance> getAll() {
        return contratReassuranceRepository.findAll();
    }

    @Override
    public Page<ContratReassurance> getAllPaginated(Pageable pageable) {
        return contratReassuranceRepository.findAll(pageable);
    }

    @Override
    public List<ContratReassurance> searchContrats(Long idContrat, String name, Date date){
    Specification<ContratReassurance> spec = Specification.where(null);

        if (idContrat != null) {
        spec = spec.and(ContratReassuranceSpecification.hasId(idContrat));
    }
        if (name != null && !name.isEmpty()) {
        spec = spec.and(ContratReassuranceSpecification.hasName(name));
    }
        if (date != null) {
        spec = spec.and(ContratReassuranceSpecification.hasDate(date));
    }

        return contratReassuranceRepository.findAll(spec);
}


}
