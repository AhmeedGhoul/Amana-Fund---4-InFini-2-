package com.ghoul.AmanaFund.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationKarimService {

    @Value("${sendinblue.api.key}")
    private String sendinblueApiKey;

    private static final String SEND_EMAIL_URL = "https://api.sendinblue.com/v3/smtp/email";

    private final RestTemplate restTemplate;

    // Constructor-based injection for RestTemplate
    public NotificationKarimService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendEmailNotification(String subject, String body, String recipientEmail) {
        // Set headers for the API request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", sendinblueApiKey);

        // Prepare email payload
        String emailPayload = "{"
                + "\"sender\": {\"email\": \"maullamebeba@gmail.com\"},"
                + "\"to\": [{\"email\": \"" + recipientEmail + "\"}],"
                + "\"subject\": \"" + subject + "\","
                + "\"htmlContent\": \"" + escapeHtml(getEngagingEmailBody(body)) + "\""
                + "}";

        // Create request entity
        HttpEntity<String> requestEntity = new HttpEntity<>(emailPayload, headers);

        // Send the email request
        try {
            ResponseEntity<String> response = restTemplate.exchange(SEND_EMAIL_URL, HttpMethod.POST, requestEntity, String.class);

            // Check if response status is CREATED (201) instead of OK (200)
            if (response.getStatusCode() == HttpStatus.CREATED) {
                System.out.println("🎉 Email successfully queued for sending. Message ID: " + extractMessageId(response.getBody()));
            } else {
                System.out.println("❌ Failed to send email. HTTP Status: " + response.getStatusCode());
                System.out.println("Response Body: " + response.getBody());  // Log response body for debugging
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error sending email: " + e.getMessage());
        }
    }

    // Utility method to escape HTML characters (like quotes) in the email body
    private String escapeHtml(String input) {
        return input.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    // Utility method to extract messageId from response body
    private String extractMessageId(String responseBody) {
        // Assuming the responseBody contains "messageId":"<some_id>"
        int startIndex = responseBody.indexOf("\"messageId\":\"") + "\"messageId\":\"".length();
        int endIndex = responseBody.indexOf("\"", startIndex);
        return responseBody.substring(startIndex, endIndex);
    }

    // Improved method to create a more professional and polished email body with clean styling
    private String getEngagingEmailBody(String bodyContent) {
        return "<html>" +
                "<head><style>" +
                "body { font-family: Arial, sans-serif; color: #333; background-color: #f4f7fa; margin: 0; padding: 0; }" +
                "h2 { color: #1a73e8; font-size: 24px; margin-bottom: 16px; }" +
                "p { font-size: 16px; color: #555; line-height: 1.5; }" +
                "table { width: 100%; border-collapse: collapse; margin-top: 20px; }" +
                "table td, table th { padding: 12px; border: 1px solid #ddd; font-size: 14px; text-align: left; }" +
                "strong { color: #1a73e8; }" +
                "hr { border: 1px solid #1a73e8; margin: 20px 0; }" +
                ".footer { font-size: 14px; color: #999; margin-top: 20px; text-align: center; }" +
                "</style></head>" +
                "<body>" +
                "<div style='padding: 20px;'>" +
                "<h2>📢 Important Update from AmanaFund</h2>" +
                "<p>Dear user,</p>" +
                "<p>We wanted to inform you about the following update:</p>" +
                bodyContent +
                "<hr>" +
                "<p>Thank you for your continued support. If you have any questions or require assistance, please don't hesitate to reach out to us.</p>" +
                "<p class='footer'>Best regards,<br>The AmanaFund Team</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
