package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.DTO.PersonDTO;
import com.ghoul.AmanaFund.entity.Person;
import com.ghoul.AmanaFund.entity.Police;
import com.ghoul.AmanaFund.service.PersonService;
import com.ghoul.AmanaFund.service.PoliceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/person")
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
    public List<PersonDTO> GetAllPerson()
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
    @GetMapping("/paginated")
    public Page<PersonDTO> getPaginatedPerson(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "age") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        // Allow only "start" or "end" for sorting
        List<String> allowedSortFields = Arrays.asList("name", "age" , "revenue");
        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field. Choose between 'name' or 'age' or 'revenue'.");
        }

        // Apply sorting direction
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return personService.getAllPaginated(pageable);
    }
}
