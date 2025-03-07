package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Garantie;
import com.ghoul.AmanaFund.entity.Person;
import com.ghoul.AmanaFund.entity.Police;
import com.ghoul.AmanaFund.repository.IgarantieRepository;
import com.ghoul.AmanaFund.repository.IpersonRepository;
import com.ghoul.AmanaFund.repository.IpoliceRepository;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PersonService implements IpersonService{
    @Autowired
    private IpersonRepository ipersonRepository;
    @Override
    public Person addGPerson(Person person) {
        if (person==null)
            throw new RuntimeException("Person should not be null");
        return ipersonRepository.save(person);
    }
    @Override
    public List<Person> retrievePersons() {
        return ipersonRepository.findAll();
    }
    @Override
    public Person updatePerson(Person person) {
        if (person.getPolice() != null) {
            Hibernate.initialize(person.getPolice());
        }
        else
            throw new RuntimeException("polie is null");
        return ipersonRepository.save(person);
    }
    @Override
    public Person retrievePerson(Long idPerson) {
        return ipersonRepository.findById(idPerson).orElse(null);
    }
    @Override
    public void removePerson(Long idPerson) {
        ipersonRepository.deleteById(idPerson);
    }

    @Override
    public Page<Person> getAllPaginated(Pageable pageable) {
        return ipersonRepository.findAll(pageable);
    }

    public void CalculatePremium()
    {

    }
}
