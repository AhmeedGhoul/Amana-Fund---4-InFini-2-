package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.Dao.AuthenticationRequest;
import com.ghoul.AmanaFund.Dao.AuthenticationResponse;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class    AuthenticationService {
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
                .userScore(0)
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
       sendValidationSms(user);
    }


    public AuthenticationResponse ResetPassword(String token, String password) {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            throw new RuntimeException("Token has expired. Request a new reset link.");
        }

        Users user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);

        var claims = new HashMap<String, Object>();
        claims.put("fullName", user.getName());
        var jwtToken = jwtService.generateToken(claims, user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
        public AuthenticationResponse activateAccount(String token)  {
            Token savedToken = tokenRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Invalid token"));
            if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
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




    public void deleteUser(@Valid int User) {
        var userr = userRepository.findById(User).orElseThrow(() -> new IllegalStateException("USER NOT FOUND"));
        if (userr != null) {

            var updatedUser = Users.builder()
                    .id(userr.getId())
                    .email(userr.getEmail())
                    .firstName(userr.getFirstName())
                    .lastName(userr.getLastName())
                    .email(userr.getEmail())
                    .password(passwordEncoder.encode(userr.getPassword()))
                    .accountLocked(userr.getAccountLocked())
                    .accountDeleted(true)
                    .age(userr.getAge())
                    .userScore(userr.getUserScore())
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
    public void modifyPassword(int userId, String oldPassword, String newPassword) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password.");
        }

        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
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
                    .password(userr.getPassword())
                    .accountLocked(userr.getAccountLocked())
                    .accountDeleted(userr.getAccountDeleted())
                    .age(User.getAge())
                    .userScore(User.getUserScore())
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
    public List<Users> getAllUsers()
    {
        return userRepository.findAll();
    }
    public Page<Users> getAllUsersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        return userRepository.findAll(pageable);
    }

    public Page<Users> searchUsersPaginated(
            String firstName, String lastName, String email,
            Integer age, String phoneNumber, LocalDate dateOfBirth, Boolean enabled, List<String> sortBy, int page, int size) {
        Specification<Users> spec = UserSpecification.searchUsers(firstName, lastName, email, age, phoneNumber, dateOfBirth, enabled);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        if (sortBy != null && !sortBy.isEmpty()) {
            List<Sort.Order> orders = new ArrayList<>();
            for (String field : sortBy) {
                if (field.startsWith("-")) {
                    orders.add(new Sort.Order(Sort.Direction.DESC, field.substring(1)));
                } else {
                    orders.add(new Sort.Order(Sort.Direction.ASC, field));
                }
            }
            pageable = PageRequest.of(page, size, Sort.by(orders));
        }

        return userRepository.findAll(spec, pageable);
    }
    public String generateUserReport(String directoryPath, String fileName) throws IOException {
        List<Users> users = userRepository.findAll();

        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            directoryPath = "C:/Users/ahmed/Downloads";
        }
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "user_report.xlsx";
        }
        String filePath = directoryPath + "/" + fileName;
        File file = new File(filePath);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("User Report");
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "First Name", "Last Name", "Email", "Age", "Address", "Civil Status", "Phone Number", "Date of Birth", "Enabled", "Account Deleted", "Account Locked", "Created Date", "Last Modified Date"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        int rowNum = 1;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Users user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getFirstName());
            row.createCell(2).setCellValue(user.getLastName());
            row.createCell(3).setCellValue(user.getEmail());
            row.createCell(4).setCellValue(user.getAge());
            row.createCell(5).setCellValue(user.getAddress());
            row.createCell(6).setCellValue(user.getCivilStatus().toString());
            row.createCell(7).setCellValue(user.getPhoneNumber());
            row.createCell(8).setCellValue(user.getDateOfBirth().toString());
            row.createCell(9).setCellValue(user.getEnabled() ? "Yes" : "No");
            row.createCell(10).setCellValue(user.getAccountDeleted() ? "Yes" : "No");
            row.createCell(11).setCellValue(user.getAccountLocked() ? "Yes" : "No");
            row.createCell(12).setCellValue(user.getCreatedDate().toString());
            row.createCell(13).setCellValue(user.getLastModifiedDate() != null ? user.getLastModifiedDate().toString() : "N/A");
        }
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }
        return filePath;
    }
    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getLockedAccountsPercentage() {
        long totalUsers = userRepository.count();
        long lockedUsers = userRepository.countByEnabled(true);
        return (lockedUsers * 100) / totalUsers;
    }

    public void resetPassword(String email) throws MessagingException {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
        sendResetEmail(user);
        //sendValidationSms(user);

    }

    private void sendResetEmail(Users user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        String activationUrl = "http://localhost:4200/authentication/reset-password?token=" + newToken;

        emailService.sendEmail(
                user.getEmail(),
                EmailTemplateName.RESET_PASSWORD,
                activationUrl,
                newToken,
                "Password Reset"
        );
    }
}
