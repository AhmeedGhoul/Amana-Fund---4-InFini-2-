package com.ghoul.AmanaFund.auth;

import com.ghoul.AmanaFund.activityLog.ActivityService;
import com.ghoul.AmanaFund.activityLog.ActivityLog;
import com.ghoul.AmanaFund.user.Users;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("auth")
@Tag(name = "Authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    private final ActivityService actService;
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest request) throws MessagingException {
    if(service.registerUser(request))
        actService.save(new ActivityLog("new user","registring user for first time","nothing", "Register User",LocalDateTime.now(),service.findUserByEmail(request.getEmail())));
    else
        actService.save(new ActivityLog("new user","registring user for first time","nothing", "Register User",LocalDateTime.now(),service.findUserByEmail(request.getEmail())));
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @PostMapping("/Promote")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> grantRole(@Valid @RequestBody GrantRoleRequest grantRoleRequest) throws MessagingException {
        if(service.grantRole(grantRoleRequest.getEmail(),grantRoleRequest.getRole()))
            actService.save(new ActivityLog("new user","registring user for first time","nothing", "Register User",LocalDateTime.now(),service.findUserByEmail(grantRoleRequest.getEmail())));
        else
            actService.save(new ActivityLog("new user","registring user for first time","nothing", "Register User",LocalDateTime.now(),service.findUserByEmail(grantRoleRequest.getEmail())));
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @PostMapping("/Demote")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> DeleteRole(@Valid @RequestBody GrantRoleRequest grantRoleRequest) throws MessagingException {
        service.deleteRoleAsignedToUser(grantRoleRequest.getEmail(),grantRoleRequest.getRole());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @DeleteMapping("/Delete")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> DeleteUser( @RequestBody Users user) throws MessagingException {
        service.deleteUser(user);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @GetMapping("/users")
    public ResponseEntity<List<Users>> showUsers() throws MessagingException {
        List<Users> users = service.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/Modify")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> ModifyUser( @RequestBody Users user) throws MessagingException {
        service.modifyUser(user);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
    @GetMapping("/activate-account")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        service.activateAccount(token);
    }

}
