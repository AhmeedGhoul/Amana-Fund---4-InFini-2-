package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Garantie;
import com.ghoul.AmanaFund.entity.Police;
import com.ghoul.AmanaFund.repository.IgarantieRepository;
import com.ghoul.AmanaFund.repository.IpoliceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class GarantieService implements IgarantieService{
    @Autowired
    private IgarantieRepository igarantieRepository;
    @Autowired
    private IpoliceRepository policeRepository;
    @Override
    public Garantie addGarantie(Garantie garantie) {
//        Police police = policeRepository.findById(garantie.getPolice().getIdPolice())
//                .orElseThrow(() -> new EntityNotFoundException("Police not found"));

        // Persist the garantie (this should persist the associated police too)
        System.out.println("****************************Received policeId: " + garantie.getPolice().getIdPolice());
        Police police = policeRepository.findById(garantie.getPolice().getIdPolice())
                .orElseThrow(() -> new EntityNotFoundException("Police with ID " + garantie.getPolice().getIdPolice() + " not found"));

        Garantie garantiee = new Garantie();
        garantiee.setActive(true);
        garantiee.setPolice(police); // Attach Police entity

        System.out.println("Saving Garantie: " + garantiee);
        return igarantieRepository.save(garantiee);
    }

    @Override
    public List<Garantie> retrieveGaranties() {
        return igarantieRepository.findAll();
    }

    @Override
    public Garantie updateGarantie(Garantie garantie) {
        if (garantie==null)
            throw new RuntimeException("Garantie should not be null");
        if (garantie.getPolice()==null)
            throw new RuntimeException("Police should not be null");
        return igarantieRepository.save(garantie);
    }

    @Override
    public Garantie retrieveGarantie(Long idGarantie) {
        if (idGarantie==null)
            throw new RuntimeException("IdGarantie should not be null");
        return igarantieRepository.findById(idGarantie).orElseThrow(() -> new RuntimeException("IdGarantie not found"));
    }

    @Override
    public void removeGarantie(Long idGarantie) {
        if (idGarantie==null)
            throw new RuntimeException("IdGarantie should not be null");
        igarantieRepository.deleteById(idGarantie);
    }
}
