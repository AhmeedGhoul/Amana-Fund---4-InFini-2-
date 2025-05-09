package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.DTO.ObjectGDTO;
import com.ghoul.AmanaFund.entity.ObjectG;
import com.ghoul.AmanaFund.entity.Person;
import com.ghoul.AmanaFund.service.DTOObjectMapper;
import com.ghoul.AmanaFund.service.ObjectGDTOMapper;
import com.ghoul.AmanaFund.service.ObjectService;
import com.ghoul.AmanaFund.service.PersonDTOMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/object")
public class ObjectController {
    private final ObjectGDTOMapper objectGDTOMapper;
    @Autowired
    private ObjectService objectService;
    @PostMapping("/add_object")
    public ObjectG addObject(@RequestBody ObjectGDTO objectGDTO)
    {
        return objectService.addDTOObject(objectGDTO);
    }
    @GetMapping("/getall_object")
    public List<ObjectGDTO> GetAllObject()
    {
        return objectService.retrieveObjectGs();
    }
    @PutMapping("/update_object")
    public ObjectGDTO updateObject(@RequestBody ObjectGDTO objectGDTO)
    {
        if (objectGDTO == null) {
            throw new RuntimeException("Object should have value");
        }
        ObjectG updatedobject = objectService.updateObjectGFromDTO(objectGDTO);
        return objectGDTOMapper.apply(updatedobject);
    }

    @DeleteMapping("/remove_object/{id}")
    public void removeObject(@PathVariable long id) {
        objectService.removeObjectG(id);
    }
    @GetMapping("/paginated")
    public Page<ObjectGDTO> getPaginatedPerson(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "ownershipCertifNumber") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        // Allow only "start" or "end" for sorting
        List<String> allowedSortFields = Arrays.asList("estimatedValue", "ownershipCertifNumber" , "type");
        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field. Choose between 'name' or 'age' or 'revenue'.");
        }

        // Apply sorting direction
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return objectService.getAllPaginated(pageable);
    }
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ObjectG> deactivatePerson(@PathVariable Long id) {
        try {
            ObjectG updatedPolice = objectService.deactivatePerson(id);
            return new ResponseEntity<>(updatedPolice, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
