package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.Dao.AuthenticationRequest;
import com.ghoul.AmanaFund.Dao.GrantRoleRequest;
import com.ghoul.AmanaFund.Dao.RegistrationRequest;
import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.repository.UserRepository;
import com.ghoul.AmanaFund.security.JwtService;
import com.ghoul.AmanaFund.service.ActivityService;
import com.ghoul.AmanaFund.service.AuthenticationService;
import com.ghoul.AmanaFund.service.IpGeolocationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("auth")
@Tag(name = "Authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final IpGeolocationService ipGeolocationService;
    private final AuthenticationService authService;
    private final ActivityService activityService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Void> createUser(@Valid @RequestBody RegistrationRequest request) throws IOException, MessagingException {
        authService.registerUser(request);
        String ipAddress = ipGeolocationService.getIpFromIpify();
        String country = ipGeolocationService.getCountryFromGeolocationApi(ipAddress);
        activityService.save(new ActivityLog(
                "User Creation",
                "User Registration succeeded",
                LocalDateTime.now(),
                null,
                null,
                ipAddress,
                country
        ));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/Promote")
    public ResponseEntity<Void> grantRoleUser(@Valid @RequestBody GrantRoleRequest grantRoleRequest, @RequestHeader("Authorization") String token) throws MessagingException, IOException {
        Users adminUser = extractUser(token);
        String ipAddress = ipGeolocationService.getIpFromIpify();
        String country = ipGeolocationService.getCountryFromGeolocationApi(ipAddress);
        authService.grantRole(grantRoleRequest.getEmail(), grantRoleRequest.getRole());
        logActivity("User Promotion", "User Promotion succeeded", adminUser,ipAddress,country);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/Demote")
    public ResponseEntity<Void> deleteRoleUser(@Valid @RequestBody GrantRoleRequest grantRoleRequest, @RequestHeader("Authorization") String token) throws MessagingException, IOException {
        Users adminUser = extractUser(token);
        String ipAddress = ipGeolocationService.getIpFromIpify();
        String country = ipGeolocationService.getCountryFromGeolocationApi(ipAddress);
        authService.deleteRoleAsignedToUser(grantRoleRequest.getEmail(), grantRoleRequest.getRole());
        logActivity("User Demotion", "User Demotion succeeded", adminUser,ipAddress,country);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/Delete")
    public ResponseEntity<Void> deleteUser(@RequestBody Users user, @RequestHeader("Authorization") String token) throws IOException {
        Users adminUser = extractUser(token);
        String ipAddress = ipGeolocationService.getIpFromIpify();
        String country = ipGeolocationService.getCountryFromGeolocationApi(ipAddress);
        authService.deleteUser(user);
        logActivity("User Delete", "User Delete succeeded", adminUser,ipAddress,country);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/users")
    public ResponseEntity<Page<Users>> showUsers(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws MessagingException, IOException {
        Users adminUser = extractUser(token);
        String ipAddress = ipGeolocationService.getIpFromIpify();
        String country = ipGeolocationService.getCountryFromGeolocationApi(ipAddress);
        Page<Users> users = authService.getAllUsersPaginated(page, size);
        logActivity("Users Preview", "User Preview succeeded", adminUser,ipAddress,country);
        return ResponseEntity.ok(users);
    }
    @PutMapping("/Modify")
    public ResponseEntity<Void> modifyUser(@RequestBody Users user, @RequestHeader("Authorization") String token) throws MessagingException, IOException {
        Users adminUser = extractUser(token);
        authService.modifyUser(user);
        String ipAddress = ipGeolocationService.getIpFromIpify();
        String country = ipGeolocationService.getCountryFromGeolocationApi(ipAddress);
        logActivity("User Modification", "User Modification succeeded", adminUser,ipAddress,country);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody AuthenticationRequest request) throws IOException {
        try {
            authService.authenticate(request);
            String ipAddress = ipGeolocationService.getIpFromIpify();
            String country = ipGeolocationService.getCountryFromGeolocationApi(ipAddress);
            Users user = authService.getUserByEmail(request.getEmail());
            logActivity("Authentication", "User Authentication succeeded", user,ipAddress,country);
            return ResponseEntity.ok(new AuthenticationResponse("Authentication succeeded. Please check your email for the 2FA code."));
        } catch (BadCredentialsException e) {
            String ipAddress = ipGeolocationService.getIpFromIpify();
            String country = ipGeolocationService.getCountryFromGeolocationApi(ipAddress);
            logActivity("Authentication", "User Authentication failed", authService.getUserByEmail(request.getEmail()),ipAddress,country);
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

    private void logActivity(String action, String description, Users user,String ipAddress,String country) {
        activityService.save(new ActivityLog(action, description, LocalDateTime.now(), user, null, ipAddress, country));
    }
    @GetMapping("/search")
    public ResponseEntity<Page<Users>> searchUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) LocalDate dateOfBirth,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) List<String> sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Users> users = authService.searchUsersPaginated(firstName, lastName, email, age, phoneNumber, dateOfBirth, enabled, sortBy, page, size);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/generateUserReport")
    public ResponseEntity<Void> generateUserReport(
            @RequestParam(required = false) String directoryPath,
            @RequestParam(required = false) String fileName) throws IOException {

        String filePath = authService.generateUserReport(directoryPath, fileName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<AuthenticationResponse> forgotPassword(@RequestParam String email) {
        try {
            authService.resetPassword(email);
            return ResponseEntity.ok(new AuthenticationResponse("Password reset link sent to your email."));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AuthenticationResponse("Email not found."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthenticationResponse("An error occurred. Please try again."));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AuthenticationResponse> resetPassword(@RequestParam String token, @RequestParam String newPassword) throws MessagingException {
        try {
            AuthenticationResponse response = authService.ResetPassword(token, newPassword);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationResponse("Invalid or expired token."));
        }
    }
}

