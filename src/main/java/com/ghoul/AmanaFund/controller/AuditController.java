package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.entity.Audit;
import com.ghoul.AmanaFund.service.AuditService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("audit")
@Tag(name = "Audit")
@RequiredArgsConstructor
public class AuditController {
    private final AuditService auditService;
        @PostMapping("/CreateAudit")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Audit> CreateAudit(@RequestBody Audit audit) {
        auditService.save(audit);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @PutMapping("/ModifyAudit")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Audit> ModifyAudit(@RequestBody Audit audit) {
        auditService.modify(audit);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @DeleteMapping("/DeleteAudit")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Audit> DeleteAudit(@RequestBody Audit audit) {
        auditService.delete(audit);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @GetMapping("/Audit")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<Audit>> getAllAudit() {

        return ResponseEntity.ok(auditService.getAllAudit());
    }



}
