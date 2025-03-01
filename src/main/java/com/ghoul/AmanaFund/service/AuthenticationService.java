package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.Dao.AuthenticationRequest;
import com.ghoul.AmanaFund.controller.AuthenticationResponse;
import com.ghoul.AmanaFund.Dao.RegistrationRequest;
import com.ghoul.AmanaFund.entity.EmailTemplateName;
import com.ghoul.AmanaFund.repository.RoleRepository;
import com.ghoul.AmanaFund.security.JwtService;
import com.ghoul.AmanaFund.entity.Token;
import com.ghoul.AmanaFund.repository.TokenRepository;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.repository.UserRepository;
import com.ghoul.AmanaFund.specification.UserSpecification;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final  SmSService smSService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void registerUser(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));

        var user = Users.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(true)
                .accountDeleted(false)
                .age(request.getAge())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .civilStatus(request.getCivilStatus())
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);
    }

    public void authenticate(AuthenticationRequest request) throws MessagingException {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = ((Users) auth.getPrincipal());
        sendValidationEmail(user);
        //sendValidationSms(user);
    }

    public AuthenticationResponse activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationSms(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been sent to the same phone number.");
        }
        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        var claims = new HashMap<String, Object>();
        claims.put("fullName", user.getName());
        var jwtToken = jwtService.generateToken(claims, user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public void grantRole(String email, String role) {
        var userRole = roleRepository.findByName(role)
                // todo - better exception handling
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));
        var userr = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("USER NOT FOUND"));
        userr.getRoles().add(userRole);
        userRepository.save(userr);

    }

    public void deleteRoleAsignedToUser(String email, String role) {
        var userRole = roleRepository.findByName(role)
                // todo - better exception handling
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));
        var userr = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("USER NOT FOUND"));
        userr.getRoles().remove(userRole);
        userRepository.save(userr);

    }


    private String generateAndSaveActivationToken(Users user) {
        String generatedToken = generateActivationCode();
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            codeBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return codeBuilder.toString();
    }




    public void deleteUser(@Valid Users User) {
        var userr = userRepository.findByEmail(User.getEmail()).orElseThrow(() -> new IllegalStateException("USER NOT FOUND"));
        if (userr != null) {

            var updatedUser = Users.builder()
                    .id(userr.getId())
                    .email(userr.getEmail())
                    .firstName(userr.getFirstName())
                    .lastName(userr.getLastName())
                    .email(userr.getEmail())
                    .password(passwordEncoder.encode(User.getPassword()))
                    .accountLocked(userr.getAccountLocked())
                    .accountDeleted(true)
                    .age(userr.getAge())
                    .phoneNumber(userr.getPhoneNumber())
                    .address(userr.getAddress())
                    .dateOfBirth(userr.getDateOfBirth())
                    .civilStatus(userr.getCivilStatus())
                    .enabled(userr.getEnabled())
                    .roles(userr.getRoles())
                    .build();
            userRepository.save(updatedUser);
        }

    }

    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void modifyUser(@Valid Users User) {
        var userr = userRepository.findByEmail(User.getEmail()).orElseThrow(() -> new IllegalStateException("USER NOT FOUND"));
        if (userr != null) {
            var updatedUser = Users.builder()
                    .id(userr.getId())
                    .email(User.getEmail())
                    .firstName(User.getFirstName())
                    .lastName(User.getLastName())
                    .email(User.getEmail())
                    .password(passwordEncoder.encode(User.getPassword()))
                    .accountLocked(userr.getAccountLocked())
                    .accountDeleted(userr.getAccountDeleted())
                    .age(User.getAge())
                    .address(User.getAddress())
                    .phoneNumber(User.getPhoneNumber())
                    .dateOfBirth(User.getDateOfBirth())
                    .civilStatus(User.getCivilStatus())
                    .enabled(userr.getEnabled())
                    .roles(userr.getRoles())
                    .build();
            userRepository.save(updatedUser);

        }


    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    private void sendValidationEmail(Users user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"


        );


    }
    public void sendValidationSms(Users user) {
        var newToken = generateAndSaveActivationToken(user);

        String smsMessage = "Your activation code is: " + newToken;

        smSService.sendSms(user.getPhoneNumber(), smsMessage);
    }
    public List<Users> searchUsers(
            String firstName, String lastName, String email,
            Integer age, String phoneNumber, LocalDate dateOfBirth, Boolean enabled, List<String> sortBy) {
        Specification<Users> spec = UserSpecification.searchUsers(firstName, lastName, email, age, phoneNumber, dateOfBirth, enabled);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        if (sortBy != null && !sortBy.isEmpty()) {
            List<Sort.Order> orders = new ArrayList<>();
            for (String field : sortBy) {
                if (field.startsWith("-")) {
                    orders.add(new Sort.Order(Sort.Direction.DESC, field.substring(1)));
                } else {
                    orders.add(new Sort.Order(Sort.Direction.ASC, field));
                }
            }
            sort = Sort.by(orders);
        }

        return userRepository.findAll(spec, sort);
    }

}
