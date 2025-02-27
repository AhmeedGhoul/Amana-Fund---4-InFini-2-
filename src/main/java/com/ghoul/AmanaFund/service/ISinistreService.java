package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.ContratReassurance;
import com.ghoul.AmanaFund.entity.Sinistre;

import java.util.List;

public interface ISinistreService {
    Sinistre add(Sinistre sinistre);
    Sinistre update(Sinistre sinistre);
    void remove(long id);
   Sinistre getById(long id);
    List<Sinistre> getAll();
}
