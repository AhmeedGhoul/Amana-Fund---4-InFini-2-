package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Garantie;
import com.ghoul.AmanaFund.entity.Person;
import com.ghoul.AmanaFund.entity.Police;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IpersonService {
    Person addGPerson(Person person);
    List<Person> retrievePersons();
    Person updatePerson(Person person);
    Person retrievePerson(Long idPerson);
    void removePerson(Long idPerson);
    public Page<Person> getAllPaginated(Pageable pageable);
}
