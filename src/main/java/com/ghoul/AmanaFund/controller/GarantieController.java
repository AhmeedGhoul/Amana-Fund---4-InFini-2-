package com.ghoul.AmanaFund.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ghoul.AmanaFund.entity.*;
import com.ghoul.AmanaFund.service.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j

@RequestMapping("Garantie")
public class GarantieController {
    @Autowired
    private GrantieService garantieService;
    @PostMapping("/add_garantie")
    public Garantie addGarantie(@RequestBody Garantie garantie)
    {
        return garantieService.addGarantie(garantie);
    }
    @GetMapping("/getall_garantie")
    public List<Garantie> GetAllGarantie()
    {
        return garantieService.retrieveGaranties();
    }
    @PutMapping("/update_garantie")
    public Garantie updateGarantie(@RequestBody Garantie garantie)
    {
        return garantieService.updateGarantie(garantie);
    }

    @DeleteMapping("/remove_garantie/{id}")
    public void removeGarantie(@PathVariable long id) {
        garantieService.removeGarantie(id);
    }
}
