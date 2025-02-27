package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.ContratReassurance;

import java.util.List;

public interface IContratReassuranceService {
    ContratReassurance add(ContratReassurance contratReassurance);
    ContratReassurance update(ContratReassurance contratReassurance);
    void remove(long id);
    ContratReassurance getById(long id);
    List<ContratReassurance> getAll();
}
