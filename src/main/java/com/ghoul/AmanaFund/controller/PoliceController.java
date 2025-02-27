package com.ghoul.AmanaFund.controller;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ghoul.AmanaFund.entity.*;
import com.ghoul.AmanaFund.service.*;

import java.awt.*;
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
