package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.DTO.PersonDTO;
import com.ghoul.AmanaFund.entity.Garantie;
import com.ghoul.AmanaFund.entity.Person;
import com.ghoul.AmanaFund.entity.Police;
import com.ghoul.AmanaFund.repository.IgarantieRepository;
import com.ghoul.AmanaFund.repository.IpersonRepository;
import com.ghoul.AmanaFund.repository.IpoliceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class PersonService implements IpersonService{
    @Autowired
    private IpoliceRepository policeRepository;
    private final PersonDTOMapper personDTOMapper;
    private final DTOPersonMapper dtoPersonMapper;

    @Autowired
    private IpersonRepository ipersonRepository;
    @Autowired
    private JavaMailSender mailSender;

    public Person deactivatePerson(Long id) {
        Person person = ipersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id " + id));
        if (person.isActive())
            person.setActive(false);
        else
            person.setActive(true);
        return ipersonRepository.save(person);
    }

    @Override
    public Person addGPerson(Person person) {
        if (person == null)
            throw new RuntimeException("Person should not be null");

        Person savedPerson = ipersonRepository.save(person);

        // Send confirmation email
        /*sendConfirmationEmail(savedPerson);*/

        return savedPerson;
    }
    public Person addDTOPerson(PersonDTO personDTO) {
        if (personDTO == null) {
            throw new RuntimeException("PersonDTO should not be null");
        }

        // Convert DTO to Entity
        Person person = dtoPersonMapper.apply(personDTO);

        // Save the entity
        Person savedPerson = ipersonRepository.save(person);

        // Optionally: Send confirmation email
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
    public Person updatePersonFromDTO(PersonDTO dto) {
        Person person = ipersonRepository.findById(dto.getIdGarantie())
                .orElseThrow(() -> new RuntimeException("Person not found"));

        person.setName(dto.getName());
        person.setLast_name(dto.getLastName());
        person.setCIN(dto.getCin());
        person.setEmail(dto.getEmail());
        person.setAge(dto.getAge());
        person.setRevenue(dto.getRevenue());
        person.setActive(dto.isActive());
        person.setDocuments(dto.getDocuments());

        if (dto.getPoliceId() != null) {
            Police police = policeRepository.findById(dto.getPoliceId())
                    .orElseThrow(() -> new RuntimeException("Police not found"));
            person.setPolice(police);
        }

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


    public Map<String, String> calculateAllPersonRiskLevels() {
        List<Person> persons = ipersonRepository.findAll();
        Map<String, String> riskLevels = new HashMap<>();

        for (Person person : persons) {
            double revenueScore = person.getRevenue() / 100;
            int ageScore = (person.getAge() >= 25 && person.getAge() <= 50) ? 30 : 10;

            int cinPrefixScore;
            try {
                String cinPrefix = person.getCIN().substring(0, 3);
                cinPrefixScore = Integer.parseInt(cinPrefix);
            } catch (Exception e) {
                riskLevels.put(person.getName() + " " + person.getLast_name(), "INVALID_CIN");
                continue;
            }

            double totalScore = revenueScore + ageScore + cinPrefixScore;
            String risk;

            if (totalScore > 130) {
                risk = "LOW";
            } else if (totalScore >= 100) {
                risk = "MEDIUM";
            } else {
                risk = "HIGH";
            }

            riskLevels.put(person.getName() + " " + person.getLast_name(), risk);
        }

        return riskLevels;
    }

    public double calculatePersonScoreById(Long id) {
        Person person = ipersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));

        double revenueScore = person.getRevenue() / 100;
        int ageScore = (person.getAge() >= 25 && person.getAge() <= 50) ? 30 : 10;

        int cinPrefixScore;
        try {
            String cinPrefix = person.getCIN().substring(0, 3);
            cinPrefixScore = Integer.parseInt(cinPrefix);
        } catch (Exception e) {
            throw new RuntimeException("Invalid CIN format for person: " + person.getCIN());
        }

        double totalScore = revenueScore + ageScore + cinPrefixScore;

        System.out.println("Score for person " + person.getName() + " (ID: " + id + ") is: " + totalScore);

        return totalScore;
    }
    public List<PersonDTO> findByCIN(String cin) {
        List<Person> persons = ipersonRepository.findAllByCIN(cin);
        if (persons.isEmpty()) {
            throw new RuntimeException("No persons found with CIN: " + cin);
        }
        return persons.stream()
                .map(personDTOMapper)
                .toList();
    }

}
