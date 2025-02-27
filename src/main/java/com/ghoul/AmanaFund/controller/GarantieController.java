package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.Garantie;
import com.ghoul.AmanaFund.entity.Police;
import com.ghoul.AmanaFund.service.IgarantieService;
import com.ghoul.AmanaFund.service.IpoliceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j


public class GarantieController {
    IgarantieService igs;
    @PostMapping("add_garantie")
    public Garantie addGarantie(@RequestBody Garantie garantie)
    {
        return igs.addGarantie( garantie);
    }
    @GetMapping("getall_Garantie")
    public List<Garantie> GetAllGarantie()
    {
        return igs.retrieveGaranties();
    }
    @PutMapping("update_Garantie")
    public Garantie updateGarantie(@RequestBody Garantie garantie)
    {
        return igs.updateGarantie(garantie);
    }

    @DeleteMapping("/remove/{id}")
    public void removeGarantie(@PathVariable long id) {
        igs.removeGarantie(id);

    }

}
