package com.ghoul.AmanaFund.controller;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.ghoul.AmanaFund.entity.*;
import com.ghoul.AmanaFund.service.*;

import java.awt.*;
import java.util.List;


@RestController
@AllArgsConstructor
@Slf4j

public class PoliceController {
    IpoliceService ipoliceService;
    @PostMapping("add_police")
    public Police addPolice(@RequestBody Police police)
    {
        return ipoliceService.addPolice(police);
    }
    @GetMapping("getall_police")
    public List<Police> GetAllPolice()
    {
        return ipoliceService.retrievePolices();
    }
    @PutMapping("update_police")
    public Police updatePolice(@RequestBody Police police)
    {
        return ipoliceService.updatePolice(police);
    }
}
