package com.ghoul.AmanaFund.controller;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import com.ghoul.AmanaFund.entity.*;
import com.ghoul.AmanaFund.service.*;

import java.awt.*;
import java.util.Arrays;
import java.util.List;


@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("Police")
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
}
