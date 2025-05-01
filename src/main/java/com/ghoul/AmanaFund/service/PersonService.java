package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.DTO.PersonDTO;
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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class PersonService implements IpersonService{
    private final PersonDTOMapper personDTOMapper;
    @Autowired
    private IpersonRepository ipersonRepository;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public Person addGPerson(Person person) {
        if (person == null)
            throw new RuntimeException("Person should not be null");

        Person savedPerson = ipersonRepository.save(person);

        // Send confirmation email
        sendConfirmationEmail(savedPerson);

        return savedPerson;
    }

    private void sendConfirmationEmail(Person person) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(person.getEmail());
        message.setSubject("Welcome to Amana Fund Microinsurance");
        message.setText("Dear " + person.getName() +
                ",\n\nYour profile has been successfully registered as guarantor of client with policy ID: "
                /*+"For Client: "+person.getPolice().getIdPolice() +"\n"*/
                +person.getPolice().getIdPolice()+"\nThank you,\nAmana Fund Team");

        mailSender.send(message);
    }
    @Override
    public List<PersonDTO> retrievePersons() {
        return ipersonRepository
                .findAll()
                .stream()
                .map(personDTOMapper)
                .collect(Collectors.toList())
                ;
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
    public PersonDTO retrievePerson(Long idPerson) {
        return ipersonRepository.findById(idPerson)
                .map(personDTOMapper)
                .orElse(null);
    }
    @Override
    public void removePerson(Long idPerson) {
        ipersonRepository.deleteById(idPerson);
    }

    @Override
    public Page<PersonDTO> getAllPaginated(Pageable pageable) {
        return ipersonRepository
                .findAll(pageable) // returns Page<Person>
                .map(personDTOMapper); // maps Page<Person> to Page<PersonDTO>
    }


    public void CalculatePremium()
    {

    }
}
