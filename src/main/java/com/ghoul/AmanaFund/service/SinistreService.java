package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Sinistre;
import com.ghoul.AmanaFund.repository.SinistreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
