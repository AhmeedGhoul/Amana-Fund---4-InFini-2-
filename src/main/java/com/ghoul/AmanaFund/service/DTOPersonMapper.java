package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.DTO.PersonDTO;
import com.ghoul.AmanaFund.entity.Person;
import com.ghoul.AmanaFund.entity.Police;
import com.ghoul.AmanaFund.repository.IpoliceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class DTOPersonMapper implements Function<PersonDTO, Person> {

    @Autowired
    private PoliceService policeService; // or use a repository if needed

    @Override
    public Person apply(PersonDTO dto) {
        Police police = policeService.getPoliceById(dto.getPoliceId());

        Person person = Person.builder()
                .name(dto.getName())
                .last_name(dto.getLastName())
                .CIN(dto.getCin())
                .email(dto.getEmail())
                .age(dto.getAge())
                .revenue(dto.getRevenue())
                .Active(dto.isActive())
                .Documents(dto.getDocuments())
                .police(police)
                .build();

        // Set inherited field manually
        person.setIdGarantie(dto.getIdGarantie());

        return person;
    }

}
