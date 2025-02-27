package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.ContratReassurance;
import com.ghoul.AmanaFund.entity.Sinistre;
import com.ghoul.AmanaFund.service.IContratReassuranceService;
import com.ghoul.AmanaFund.service.ISinistreService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
public class ContratReassuranceController {
    private IContratReassuranceService RS;
    @PostMapping("/addReassurance")
    public ContratReassurance addReassurance(@RequestBody ContratReassurance contratReassurance)
    { RS.add(contratReassurance);

        return contratReassurance;
    }
    @PutMapping("/updateReassurance")
    public ContratReassurance  updateReassurance(@RequestBody ContratReassurance contratReassurance) {
        return RS.update(contratReassurance);
    }
    @DeleteMapping("/removeReassurance/{id}")
    public void removeReassurance(@PathVariable long id) {
        RS.remove(id);

    }
    @GetMapping("/getReassurance/{id}")
    public ContratReassurance getReassuranceById(@PathVariable long id) {
        return RS.getById(id);
    }
    @GetMapping("Reassurance/all")
    public List<ContratReassurance> getAllContrat() {
        return RS.getAll();
    }
}
