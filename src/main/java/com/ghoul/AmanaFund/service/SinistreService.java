package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.Recherche.SinistreSpecification;
import com.ghoul.AmanaFund.entity.Sinistre;
import com.ghoul.AmanaFund.repository.SinistreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SinistreService implements  ISinistreService{
    private final SinistreRepository sinistreRepository;
    @Override
    public Sinistre add(Sinistre sinistre) {
        return sinistreRepository.save(sinistre);
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


}
