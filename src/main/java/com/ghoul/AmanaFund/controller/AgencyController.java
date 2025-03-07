package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.Governorate;
import com.ghoul.AmanaFund.service.AgencyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import com.ghoul.AmanaFund.entity.Agency;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("Agency")
@Tag(name = "Agency")
public class AgencyController {
    @Autowired
    private AgencyService agencyService;

    @PostMapping("add_agency")
    public Agency addAgency(@Valid @RequestBody Agency agency) {
        return agencyService.addAgency(agency);
    }

    @GetMapping("getall_agency")
    public List<Agency> getAllAgencies() {
        return agencyService.retrieveAgencies();
    }

    @PutMapping("update_agency")
    public Agency updateAgency(@Valid @RequestBody Agency agency) {
        return agencyService.updateAgency(agency);
    }

    @GetMapping("get_agency/{id_agency}")
    public Agency getAgencyById(@PathVariable Integer id_agency) {
        return agencyService.retrieveAgency(id_agency);
    }

    @DeleteMapping("remove_agency/{id_agency}")
    public void removeAgency(@PathVariable Integer id_agency) {
        agencyService.removeAgency(id_agency);
    }

    // Updated: Advanced search for city filtering
    @GetMapping("filter/city")
    public List<Agency> filterByCity(@RequestParam String city) {
        return agencyService.searchByCity(city);
    }

    @GetMapping("filter/governorate")
    public List<Agency> filterByGovernorate(@RequestParam Governorate governorate) {
        return agencyService.filterByGovernorate(governorate);
    }

    @GetMapping("paginate")
    public Page<Agency> getAgenciesWithPagination(@RequestParam int page, @RequestParam int size) {
        return agencyService.getAgenciesWithPagination(page, size);
    }
}