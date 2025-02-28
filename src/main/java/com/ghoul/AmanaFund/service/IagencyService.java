package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Agency;
import com.ghoul.AmanaFund.entity.Governorate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IagencyService {
    Agency addAgency(Agency agency);
    List<Agency> retrieveAgencies();
    Agency updateAgency(Agency agency);
    Agency retrieveAgency(Integer id_agency);
    void removeAgency(Integer id_agency);
    List<Agency> searchByCity(String city);  // Updated method for advanced search
    List<Agency> filterByGovernorate(Governorate governorate);
    Page<Agency> getAgenciesWithPagination(int page, int size);
}
