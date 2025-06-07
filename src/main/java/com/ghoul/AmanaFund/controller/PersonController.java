package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.DTO.PersonDTO;
import com.ghoul.AmanaFund.entity.Person;
import com.ghoul.AmanaFund.entity.Police;
import com.ghoul.AmanaFund.service.PersonDTOMapper;
import com.ghoul.AmanaFund.service.PersonService;
import com.ghoul.AmanaFund.service.PoliceService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/person")
public class PersonController {
    private final PersonDTOMapper personDTOMapper;
    @Autowired
    private PersonService personService;
    @PostMapping(value = "/add_personG", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Person> addPerson(
            @RequestPart("person") PersonDTO person,
            @RequestPart("file") MultipartFile file) throws IOException {

        if (person == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Create directory if not exists
        String uploadDir = "uploads/person-documents/";
        Files.createDirectories(Paths.get(uploadDir));

        // Generate filename and write file
        String filename = "person_" + person.getCin() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, filename);
        Files.write(filePath, file.getBytes());

        // Set the relative file path (you can customize)
        person.setDocuments(filePath.toString());

        // Save and return
        Person saved = personService.addDTOPerson(person);
        return ResponseEntity.ok(saved);
    }

    @PutMapping(value = "/update_person", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PersonDTO> updatePerson(
            @RequestPart("person") PersonDTO personDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        if (personDTO == null) {
            return ResponseEntity.badRequest().build();
        }

        // Handle file upload if a file is provided
        if (file != null && !file.isEmpty()) {
            String uploadDir = "uploads/person-documents/";
            Files.createDirectories(Paths.get(uploadDir));

            String filename = "person_" + personDTO.getCin() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, filename);
            Files.write(filePath, file.getBytes());

            // Set document path in DTO
            personDTO.setDocuments(filePath.toString());
        }

        // Perform the update using your service
        Person updatedPerson = personService.updatePersonFromDTO(personDTO);

        return ResponseEntity.ok(personDTOMapper.apply(updatedPerson));
    }

    @GetMapping("/{id}/document")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id, HttpServletResponse response) {
        try {
            PersonDTO person = personService.retrievePerson(id);

            if (person.getDocuments() == null || person.getDocuments().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = Paths.get(person.getDocuments());

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null) mimeType = "application/pdf";

            // Remove X-Frame-Options header for this response
/*            response.setHeader("X-Frame-Options", "");*/

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath.getFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getall_person")
    public List<PersonDTO> GetAllPerson()
    {
        return personService.retrievePersons();
    }
    @GetMapping("/risk-levels")
    public Map<String, String> getAllRiskLevels() {
        return personService.calculateAllPersonRiskLevels();
    }
    @GetMapping("/{id}/score")
    public double getPersonScoreById(@PathVariable Long id) {
        return personService.calculatePersonScoreById(id);
    }

    @GetMapping("/search-by-cin")
    public List<PersonDTO> searchPersonByCIN(@RequestParam String cin) {
        return personService.findByCIN(cin);
    }

    @DeleteMapping("/remove_person/{id}")
    public void removePerson(@PathVariable long id) {
        personService.removePerson(id);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Person> deactivatePerson(@PathVariable Long id) {
        try {
            Person updatedPolice = personService.deactivatePerson(id);
            return new ResponseEntity<>(updatedPolice, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
