package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.Sinistre;
import com.ghoul.AmanaFund.service.ISinistreService;
import com.ghoul.AmanaFund.service.SinistreService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
public class SinitreController {
   private  ISinistreService SS;
    @PostMapping("/addSinistre")
    public Sinistre addSinistre(@RequestBody Sinistre sinistre)
    { SS.add(sinistre);

        return sinistre;
    }
    @PutMapping("/updateSinistre")
    public Sinistre updateSinistre(@RequestBody Sinistre sinistre) {
        return SS.update(sinistre);
    }
    @DeleteMapping("/remove/{id}")
    public void removeSinistre(@PathVariable long id) {
        SS.remove(id);

    }
    @GetMapping("/getSinistre/{id}")
    public Sinistre getSinistreById(@PathVariable long id) {
        return SS.getById(id);
    }
    @GetMapping("Sinistre/all")
    public List<Sinistre> getAllSinistre() {
        return SS.getAll();
    }

}
