package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Governorate;
import com.ghoul.AmanaFund.repository.IagencyRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.ghoul.AmanaFund.entity.Agency;
import java.util.List;

@Service
@AllArgsConstructor
public class AgencyService implements IagencyService {
    @Autowired
    private IagencyRepository iagencyRepository;

    @Override
    public Agency addAgency(Agency agency) {
        return iagencyRepository.save(agency);
    }

    @Override
    public List<Agency> retrieveAgencies() {
        return iagencyRepository.findAll();
    }

    @Override
    public Agency updateAgency(Agency agency) {
        return iagencyRepository.save(agency);
    }

    @Override
    public Agency retrieveAgency(Integer id_agency) {
        return iagencyRepository.findById(id_agency).orElse(null);
    }

    @Override
    public void removeAgency(Integer id_agency) {
        iagencyRepository.deleteById(id_agency);
    }

    @Override
    public List<Agency> searchByCity(String city) {
        return iagencyRepository.searchByCity(city);
    }

    @Override
    public List<Agency> filterByGovernorate(Governorate governorate) {
        return iagencyRepository.findByGovernorate(governorate);
    }

    @Override
    public Page<Agency> getAgenciesWithPagination(int page, int size) {
        return iagencyRepository.findAll(PageRequest.of(page - 1, size));
    }
}
