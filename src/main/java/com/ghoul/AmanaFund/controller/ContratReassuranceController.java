package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.ContratReassurance;
import com.ghoul.AmanaFund.entity.Sinistre;
import com.ghoul.AmanaFund.service.IContratReassuranceService;
import com.ghoul.AmanaFund.service.ISinistreService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("Contract")
public class ContratReassuranceController {
    private IContratReassuranceService RS;
    @PostMapping("/addReassurance")
    public ContratReassurance addReassurance(@Valid @RequestBody ContratReassurance contratReassurance)
    { RS.add(contratReassurance);

        return contratReassurance;
    }
    @PutMapping("/updateReassurance")
    public ContratReassurance  updateReassurance(@Valid @RequestBody ContratReassurance contratReassurance) {
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
    @GetMapping("Reassurance/paginated")
    public Page<ContratReassurance> getPaginatedContracts(
            @PageableDefault(size = 5, sort = "date") Pageable pageable) {
        return RS.getAllPaginated(pageable);
    }
    @GetMapping("/searchReassurance")
    public ResponseEntity<List<ContratReassurance>> searchContrats(
            @RequestParam(required = false) Long idContrat,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date
    ) {
        return ResponseEntity.ok(RS.searchContrats(idContrat, name, date));
    }
    @GetMapping("/{id}/rentabilite")
    public double getRatioRentabilite(@PathVariable Long id) {
        return RS.calculerRatioRentabilite(id);
    }
}
