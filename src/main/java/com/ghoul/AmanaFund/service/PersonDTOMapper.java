package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.DTO.PersonDTO;
import com.ghoul.AmanaFund.entity.Person;
import org.springframework.stereotype.Service;

import java.util.function.Function;
@Service
public class PersonDTOMapper implements Function <Person, PersonDTO> {
    @Override
    public PersonDTO apply(Person person) {
        return new PersonDTO(
                person.getIdGarantie(),
                person.getName(),
                person.getLast_name(),
                person.getCIN(),
                person.getEmail(),
                person.getAge(),
                person.getRevenue(),
                person.isActive(),
                person.getDocuments(),
                person.getPolice().getIdPolice()
        );
    }
}
