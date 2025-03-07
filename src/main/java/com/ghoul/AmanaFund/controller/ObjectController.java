package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.ObjectG;
import com.ghoul.AmanaFund.entity.Person;
import com.ghoul.AmanaFund.service.ObjectService;
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
@RequestMapping("/object")
public class ObjectController {
    @Autowired
    private ObjectService objectService;
    @PostMapping("/add_object")
    public ObjectG addObject(@RequestBody ObjectG objectG)
    {
        return objectService.addGObjectG(objectG);
    }
    @GetMapping("/getall_object")
    public List<ObjectG> GetAllObject()
    {
        return objectService.retrieveObjectGs();
    }
    @PutMapping("/update_object")
    public ObjectG updateObject(@RequestBody ObjectG objectG)
    {
        return objectService.updateObjectG(objectG);
    }

    @DeleteMapping("/remove_object/{id}")
    public void removeObject(@PathVariable long id) {
        objectService.removeObjectG(id);
    }
    @GetMapping("/paginated")
    public Page<ObjectG> getPaginatedPerson(
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

        return objectService.getAllPaginated(pageable);
    }
}
