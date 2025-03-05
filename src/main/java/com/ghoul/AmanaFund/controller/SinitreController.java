package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.ContratReassurance;
import com.ghoul.AmanaFund.entity.Sinistre;
import com.ghoul.AmanaFund.service.ISinistreService;
import com.ghoul.AmanaFund.service.SinistreService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
public class SinitreController {
    private final ISinistreService sinistreService;

    @PostMapping("/add")
    public Sinistre addSinistre(@Valid @RequestBody Sinistre sinistre) {
        return sinistreService.add(sinistre);
    }

    @PutMapping("/update")
    public Sinistre updateSinistre(@Valid @RequestBody Sinistre sinistre) {
        return sinistreService.update(sinistre);
    }

    @DeleteMapping("/remove/{id}")
    public void removeSinistre(@PathVariable long id) {
        sinistreService.remove(id);
    }

    @GetMapping("/{id}")
    public Sinistre getSinistreById(@PathVariable long id) {
        return sinistreService.getById(id);
    }

    @GetMapping("/all")
    public List<Sinistre> getAllSinistre() {
        return sinistreService.getAll();
    }
    @GetMapping("Sinistre/paginated")
    public Page<Sinistre> getPaginatedContracts(
            @PageableDefault(size = 5, sort = "date") Pageable pageable) {
        return sinistreService.getAllPaginated(pageable);
    }
    @GetMapping("/search")
    public ResponseEntity<List<Sinistre>> searchSinistres(
            @RequestParam(required = false) Long idSinistre,
            @RequestParam(required = false) Double claimAmount,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date settlementDate,
            @RequestParam(required = false) Double settlementAmount
    ) {
        return ResponseEntity.ok(sinistreService.searchSinistres(idSinistre, claimAmount, settlementDate, settlementAmount));
    }


}
