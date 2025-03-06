package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Person;
import com.ghoul.AmanaFund.repository.IpersonRepository;
import com.ghoul.AmanaFund.repository.IpoliceRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PersonService implements IpersonService{
    @Autowired
    private IpersonRepository ipersonRepository;
    @Override
    public Person addGPerson(Person person) {
        return ipersonRepository.save(person);
    }

    @Override
    public List<Person> retrievePersons() {
        return ipersonRepository.findAll();
    }

    @Override
    public Person updatePerson(Person person) {
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
    public void CalculatePremium()
    {

    }
}
