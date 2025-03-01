package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.Dao.AuthenticationRequest;
import com.ghoul.AmanaFund.Dao.GrantRoleRequest;
import com.ghoul.AmanaFund.Dao.RegistrationRequest;
import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.security.JwtService;
import com.ghoul.AmanaFund.service.ActivityService;
import com.ghoul.AmanaFund.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("auth")
@Tag(name = "Authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;
    private final ActivityService activityService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<Void> createUser(@Valid @RequestBody RegistrationRequest request) throws MessagingException {
  authService.registerUser(request);
        activityService.save(new ActivityLog("User Creation", "User Registration succeeded", LocalDateTime.now(), null,null));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/Promote")
    public ResponseEntity<Void> grantRoleUser(@Valid @RequestBody GrantRoleRequest grantRoleRequest, @RequestHeader("Authorization") String token) throws MessagingException {
        Users adminUser = extractUser(token);
        authService.grantRole(grantRoleRequest.getEmail(), grantRoleRequest.getRole());
        logActivity("User Promotion", "User Promotion succeeded", adminUser);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/Demote")
    public ResponseEntity<Void> deleteRoleUser(@Valid @RequestBody GrantRoleRequest grantRoleRequest, @RequestHeader("Authorization") String token) throws MessagingException {
        Users adminUser = extractUser(token);
        authService.deleteRoleAsignedToUser(grantRoleRequest.getEmail(), grantRoleRequest.getRole());
        logActivity("User Demotion", "User Demotion succeeded", adminUser);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/Delete")
    public ResponseEntity<Void> deleteUser(@RequestBody Users user, @RequestHeader("Authorization") String token) {
        Users adminUser = extractUser(token);
        authService.deleteUser(user);
        logActivity("User Delete", "User Delete succeeded", adminUser);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<Users>> showUsers(@RequestHeader("Authorization") String token) throws MessagingException {
        Users adminUser = extractUser(token);
        List<Users> users = authService.getAllUsers();
        logActivity("Users Preview", "User Preview succeeded", adminUser);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/Modify")
    public ResponseEntity<Void> modifyUser(@RequestBody Users user, @RequestHeader("Authorization") String token) throws MessagingException {
        Users adminUser = extractUser(token);
        authService.modifyUser(user);
        logActivity("User Modification", "User Modification succeeded", adminUser);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody AuthenticationRequest request) {
        try {
            authService.authenticate(request);
            Users user = authService.getUserByEmail(request.getEmail());
            logActivity("Authentication", "User Authentication succeeded", user);
            return ResponseEntity.ok(new AuthenticationResponse("Authentication succeeded. Please check your email for the 2FA code."));
        } catch (BadCredentialsException e) {
            logActivity("Authentication", "User Authentication failed", authService.getUserByEmail(request.getEmail()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationResponse("Invalid credentials"));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/F2A")
    public ResponseEntity<AuthenticationResponse> verify2FACode(@RequestParam String token) throws MessagingException {
        try {
            AuthenticationResponse response = authService.activateAccount(token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationResponse("Invalid 2FA code or expired token"));
        }
    }



    private Users extractUser(String token) {
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        return authService.getUserByEmail(email);
    }

    private void logActivity(String action, String description, Users user) {
        activityService.save(new ActivityLog(action, description, LocalDateTime.now(), user, null));
    }
    @GetMapping("/search")
    public ResponseEntity<List<Users>> searchUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) LocalDate dateOfBirth,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) List<String> sortBy) {

        List<Users> users=  authService.searchUsers(firstName, lastName, email, age, phoneNumber, dateOfBirth, enabled, sortBy);
        return ResponseEntity.ok(users);
    }
}
