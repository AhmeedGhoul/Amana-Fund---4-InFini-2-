package com.ghoul.AmanaFund.handler;

import com.ghoul.AmanaFund.entity.ActivityLog;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.security.JwtService;
import com.ghoul.AmanaFund.service.ActivityService;
import com.ghoul.AmanaFund.service.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static com.ghoul.AmanaFund.handler.BusinessErrorCodes.*;
import static org.springframework.http.HttpStatus.*;

@AllArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final JwtService jwtService;
    private final AuthenticationService authService;
    private final ActivityService activityService;

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleLockedException(LockedException ex) {
        return buildErrorResponse(UNAUTHORIZED, ACCOUNT_LOCKED.getCode(), ACCOUNT_LOCKED.getDescription(), ex.getMessage());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleDisabledException(DisabledException ex) {
        return buildErrorResponse(UNAUTHORIZED, ACCOUNT_DISABLED.getCode(), ACCOUNT_DISABLED.getDescription(), ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return buildErrorResponse(UNAUTHORIZED, BAD_CREDENTIALS.getCode(), BAD_CREDENTIALS.getDescription(), ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidJson(HttpMessageNotReadableException ex, HttpServletRequest request, HandlerMethod handlerMethod) {
        Users createdByUser = extractUserFromToken(request.getHeader("Authorization"));

        String methodName = handlerMethod.getMethod().getName();
        String entityType = extractEntityType(methodName);
        String actionType = extractActionType(methodName);

        activityService.save(new ActivityLog(
                entityType + " " + actionType,
                entityType + " " + actionType + " failed due to invalid JSON",
                LocalDateTime.now(),
                createdByUser,
                null
        ));

        return buildErrorResponse(BAD_REQUEST, ACTIVITY_LOG.getCode(), "Invalid JSON format or incorrect field values", ex.getMostSpecificCause().getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request, HandlerMethod handlerMethod) {
        Users createdByUser = extractUserFromToken(request.getHeader("Authorization"));

        Set<String> errors = new HashSet<>();
        ex.getBindingResult().getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));

        String methodName = handlerMethod.getMethod().getName();
        String entityType = extractEntityType(methodName);
        String actionType = extractActionType(methodName);

        activityService.save(new ActivityLog(
                entityType + " " + actionType,
                entityType + " " + actionType + " validation failed",
                LocalDateTime.now(),
                createdByUser,
                null
        ));

        return ResponseEntity.status(BAD_REQUEST).body(
                ExceptionResponse.builder()
                        .businessErrorCode(ACTIVITY_LOG.getCode())
                        .businessErrorDescription("Validation errors occurred")
                        .validationErrors(errors)
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return buildErrorResponse(INTERNAL_SERVER_ERROR, 500, "Internal error, contact the admin", ex.getMessage());
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleMessagingException(MessagingException ex) {
        return buildErrorResponse(INTERNAL_SERVER_ERROR, 500, "Messaging error occurred", ex.getMessage());
    }


    private ResponseEntity<ExceptionResponse> buildErrorResponse(HttpStatus status, int errorCode, String description, String errorMessage) {
        return ResponseEntity.status(status).body(
                ExceptionResponse.builder()
                        .businessErrorCode(errorCode)
                        .businessErrorDescription(description)
                        .error(errorMessage)
                        .build()
        );
    }

    private Users extractUserFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            try {
                String email = jwtService.extractUsername(token.replace("Bearer ", ""));
                return authService.getUserByEmail(email);
            } catch (Exception e) {
                System.out.println("Failed to extract user: " + e.getMessage());
            }
        }
        return null;
    }

    private String extractEntityType(String methodName) {
        if (methodName.toLowerCase().contains("audit")) return "Audit";
        if (methodName.toLowerCase().contains("case")) return "Fraud Case";
        if (methodName.toLowerCase().contains("user")) return "User";
        return "Unknown";
    }

    private String extractActionType(String methodName) {
        if (Pattern.compile("(create|add|save)", Pattern.CASE_INSENSITIVE).matcher(methodName).find()) return "creation";
        if (Pattern.compile("(update|modify|edit)", Pattern.CASE_INSENSITIVE).matcher(methodName).find()) return "modification";
        if (Pattern.compile("(delete|remove)", Pattern.CASE_INSENSITIVE).matcher(methodName).find()) return "deletion";
        return "operation";
    }
}
