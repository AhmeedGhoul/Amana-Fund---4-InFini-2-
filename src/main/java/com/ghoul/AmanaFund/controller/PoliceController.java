package com.ghoul.AmanaFund.controller;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ghoul.AmanaFund.entity.*;
import com.ghoul.AmanaFund.service.*;

import java.awt.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@RestController
@AllArgsConstructor
@Slf4j

public class PoliceController {
    @Autowired
    private PoliceService policeService;
    @PostMapping("/add_police")
    public Police addPolice(@RequestBody Police police)
    {
        return policeService.addPolice(police);
    }
    @GetMapping("/getall_police")
    public List<Police> GetAllPolice()
    {
        return policeService.retrievePolices();
    }
    @GetMapping("/paginated")
    public Page<Police> getPaginatedContracts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "start") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        // Allow only "start" or "end" for sorting
        List<String> allowedSortFields = Arrays.asList("start", "end");
        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field. Choose between 'start' or 'end'.");
        }

        // Apply sorting direction
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return policeService.getAllPaginated(pageable);
    }

    @PutMapping("/update_police")
    public Police updatePolice(@RequestBody Police police)
    {
        return policeService.updatePolice(police);
    }

    @DeleteMapping("/removepolice/{id}")
    public void removePolice(@PathVariable long id) {
        policeService.removePolice(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Police>> searchPolice(
            @RequestParam(value = "start", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam(value = "amount", required = false) Double amount,
            @RequestParam(value = "id", required = false) Long id
    ) {
        try {
            List<Police> results = policeService.searchPolice(start, amount, id);
            if (results.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
