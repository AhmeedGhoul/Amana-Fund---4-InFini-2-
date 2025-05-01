package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.DTO.PersonDTO;
import com.ghoul.AmanaFund.entity.Garantie;
import com.ghoul.AmanaFund.entity.Person;
import com.ghoul.AmanaFund.entity.Police;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IpersonService {
    Person addGPerson(Person person);
    List<PersonDTO> retrievePersons();
    Person updatePerson(Person person);
    PersonDTO retrievePerson(Long idPerson);
    void removePerson(Long idPerson);
    public Page<PersonDTO> getAllPaginated(Pageable pageable);
}
