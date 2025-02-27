package com.ghoul.AmanaFund.service;
import com.ghoul.AmanaFund.repository.IgarantieRepository;
import com.ghoul.AmanaFund.entity.Garantie;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor

public class GrantieService implements IgarantieService{
    IgarantieRepository igarantieRepository;
    @Override
    public Garantie addGarantie(Garantie garantie) {
        return igarantieRepository.save(garantie);
    }

    @Override
    public List<Garantie> retrieveGaranties() {
        return null;
    }

    @Override
    public Garantie updateGarantie(Garantie garantie) {
        return null;
    }

    @Override
    public Garantie retrieveGarantie(Long idGarantie) {
        return null;
    }

    @Override
    public void removeGarantie(Long idGarantie) {

    }
}
