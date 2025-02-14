package com.ghoul.AmanaFund.fraudCase;

import com.ghoul.AmanaFund.activityLog.ActivityLog;
import com.ghoul.AmanaFund.audit.Audit;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("case")
@Tag(name = "Case")
@RequiredArgsConstructor
public class FraudCaseController {
    private final FraudCaseService service;
    @PostMapping("/CreateCase")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<FraudCases> CreateCase(@RequestBody FraudCases fraudCases) {
        service.save(fraudCases);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @PutMapping("/ModifyCase")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Audit> ModifyCase(@RequestBody FraudCases fraudCases) {
        service.modify(fraudCases);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @DeleteMapping("/DeleteCase")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Audit> DeleteCase(@RequestBody FraudCases fraudCases) {
        service.delete(fraudCases);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @GetMapping("/Case")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<FraudCases>> showFraudCases() throws MessagingException {
        List<FraudCases> fraudCases = service.findAll();
        return ResponseEntity.ok(fraudCases);
    }
}
