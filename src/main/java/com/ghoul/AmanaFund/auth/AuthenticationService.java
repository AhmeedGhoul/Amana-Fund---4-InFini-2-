package com.ghoul.AmanaFund.auth;

import com.ghoul.AmanaFund.email.EmailService;
import com.ghoul.AmanaFund.email.EmailTemplateName;
import com.ghoul.AmanaFund.role.RoleRepository;
import com.ghoul.AmanaFund.security.JwtService;
import com.ghoul.AmanaFund.user.Token;
import com.ghoul.AmanaFund.user.TokenRepository;
import com.ghoul.AmanaFund.user.Users;
import com.ghoul.AmanaFund.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
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
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public boolean registerUser(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                // todo - better exception handling
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));
        var user = Users.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        Users savedUser = userRepository.save(user);
        if (savedUser != null) {
            sendValidationEmail(savedUser);
            return true;
        }
        return false;
    }
    public boolean grantRole(String email, String role) throws MessagingException {
        var userRole = roleRepository.findByName(role)
                // todo - better exception handling
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));
        var userr= userRepository.findByEmail(email).orElseThrow(()->new IllegalStateException("USER NOT FOUND"));
        if (  userr.getRoles().add(userRole)) {
            userRepository.save(userr);
            return true;
        }
        return false;
    }
    public void deleteRoleAsignedToUser(String role,String email) throws MessagingException {
        var userRole = roleRepository.findByName(role)
                // todo - better exception handling
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));
        var userr= userRepository.findByEmail(email).orElseThrow(()->new IllegalStateException("USER NOT FOUND"));
        userr.getRoles().remove(userRole);
    }
    private void sendValidationEmail(Users user) throws MessagingException {
        var newToken= generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"



        );


    }

    private String generateAndSaveActivationToken(Users user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
   tokenRepository.save(token);
    return generatedToken;}

    private String generateActivationCode(int length) {
        String characters="0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            codeBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var claims = new HashMap<String, Object>();
        var user = ((Users) auth.getPrincipal());
        claims.put("fullName", user.getName());

        var jwtToken = jwtService.generateToken(claims, (Users) auth.getPrincipal());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    //@Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                // todo exception has to be defined
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    public void deleteUser(Users User) throws MessagingException {
        userRepository.delete(User);
    }

    public void modifyUser(Users User) throws MessagingException {
        var userr= userRepository.findByEmail(User.getEmail()).orElseThrow(()->new IllegalStateException("USER NOT FOUND"));
        if(userr!=null)
    userRepository.save(User);
    }
    public List<Users> getAllUsers() throws MessagingException {
        return userRepository.findAll();
    }
}
