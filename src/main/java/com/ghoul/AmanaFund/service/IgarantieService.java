package com.ghoul.AmanaFund.service;
import com.ghoul.AmanaFund.entity.Garantie;

import java.util.List;

public interface IgarantieService {
    Garantie addGarantie(Garantie garantie);
    List<Garantie> retrieveGaranties();
    Garantie updateGarantie(Garantie garantie);
    Garantie retrieveGarantie(Long idGarantie);
    void removeGarantie(Long idGarantie);
}

