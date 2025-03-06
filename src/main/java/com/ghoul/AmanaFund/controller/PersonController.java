package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.Person;
import com.ghoul.AmanaFund.service.PersonService;
import com.ghoul.AmanaFund.service.PoliceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class PersonController {
    @Autowired
    private PersonService personService;
    @PostMapping("/add_person")
    public Person addPerson(@RequestBody Person person)
    {
        if (person==null)
            throw new RuntimeException("Person should have value");
        return personService.addGPerson(person);
    }
    @GetMapping("/getall_person")
    public List<Person> GetAllPerson()
    {
        return personService.retrievePersons();
    }
    @PutMapping("/update_person")
    public Person updatePerson(@RequestBody Person person)
    {
        if (person==null)
            throw new RuntimeException("Person should have value");
        return personService.updatePerson(person);
    }
    @DeleteMapping("/remove_person/{id}")
    public void removePerson(@PathVariable long id) {
        personService.removePerson(id);
    }
}
