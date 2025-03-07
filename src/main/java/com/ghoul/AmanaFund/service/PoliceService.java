package com.ghoul.AmanaFund.service;
import com.ghoul.AmanaFund.entity.FrequencyPolice;
import com.ghoul.AmanaFund.repository.IpoliceRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.ghoul.AmanaFund.entity.Police;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;


@Service
@AllArgsConstructor

public class PoliceService implements IpoliceService{
    @Autowired
    private IpoliceRepository ipoliceRepository;
    @Override
    public Police addPolice(Police police) {
        if (police==null)
            throw new RuntimeException("police should not be null");
        return ipoliceRepository.save(police);
    }

    @Override
    public List<Police> retrievePolices() {
        return ipoliceRepository.findAll();
    }

    @Override
    public Police updatePolice(Police police) {
        if (police==null)
            throw new RuntimeException("police should not be null");
        return ipoliceRepository.save(police);
    }

    @Override
    public Police retrievePolice(Long idPolice) {
        return ipoliceRepository.findById(idPolice).orElseThrow(() -> new RuntimeException("idPolice not found"));
    }

    @Override
    public void removePolice(Long idPolice) {
        if (idPolice==null)
            throw new RuntimeException("idPolice should not be null");
        ipoliceRepository.deleteById(idPolice);
    }

    @Override
    public Page<Police> getAllPaginated(Pageable  pageable) {
        return ipoliceRepository.findAll(pageable);
    }

    @Override
    public List<Police> searchPolice(Date start, Double amount, Long id) {
        return ipoliceRepository.searchPolice(start, amount, id);
    }

}
