package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.ContratReassurance;
import com.ghoul.AmanaFund.repository.ContratReassuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
