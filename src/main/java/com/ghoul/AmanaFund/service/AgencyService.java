package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Agency;
import com.ghoul.AmanaFund.entity.Governorate;
import com.ghoul.AmanaFund.repository.IagencyRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AgencyService implements IagencyService {
    @Autowired
    private IagencyRepository iagencyRepository;

    @Autowired
    private GeoLocationService geoLocationService;

    @Autowired
    private TwilioService twilioService; // Inject Twilio Service

    @Override
    public Agency addAgency(Agency agency) {
        double[] coordinates = geoLocationService.getCoordinates(
                agency.getAddress(), agency.getCity(), agency.getGovernorate().name());
        agency.setLatitude(coordinates[0]);
        agency.setLongitude(coordinates[1]);
        Agency savedAgency = iagencyRepository.save(agency);

        // Send SMS Notification
//        String message = "New agency added: " + savedAgency.getCity() +
//                ", Address: " + savedAgency.getAddress();
//        twilioService.sendSms(message);

        return savedAgency;
    }

    @Override
    public List<Agency> retrieveAgencies() {
        return List.of();
    }

    public Page<Agency> retrieveAgencies(Pageable pageable) {
        return iagencyRepository.findAll(pageable);
    }

    @Override
    public Agency updateAgency(Agency agency) {
        double[] coordinates = geoLocationService.getCoordinates(
                agency.getAddress(), agency.getCity(), agency.getGovernorate().name());
        agency.setLatitude(coordinates[0]);
        agency.setLongitude(coordinates[1]);
        Agency updatedAgency = iagencyRepository.save(agency);

        // Send SMS Notification
//        String message = "Agency updated: " + updatedAgency.getCity() +
//                ", New Address: " + updatedAgency.getAddress();
//        twilioService.sendSms(message);

        return updatedAgency;
    }

    @Override
    public Agency retrieveAgency(Integer id_agency) {
        return iagencyRepository.findById(id_agency).orElse(null);
    }

    @Override
    public void removeAgency(Integer id_agency) {
        iagencyRepository.deleteById(id_agency);

        // Send SMS Notification
//        String message = "Agency with ID " + id_agency + " has been deleted.";
//        twilioService.sendSms(message);
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
