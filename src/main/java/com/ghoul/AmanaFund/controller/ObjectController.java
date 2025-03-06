package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.ObjectG;
import com.ghoul.AmanaFund.service.ObjectService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class ObjectController {
    @Autowired
    private ObjectService objectService;
    @PostMapping("/add_object")
    public ObjectG addObject(@RequestBody ObjectG objectG)
    {
        return objectService.addGObjectG(objectG);
    }
    @GetMapping("/getall_object")
    public List<ObjectG> GetAllObject()
    {
        return objectService.retrieveObjectGs();
    }
    @PutMapping("/update_object")
    public ObjectG updateObject(@RequestBody ObjectG objectG)
    {
        return objectService.updateObjectG(objectG);
    }

    @DeleteMapping("/remove_object/{id}")
    public void removeObject(@PathVariable long id) {
        objectService.removeObjectG(id);
    }
}
