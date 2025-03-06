package com.ghoul.AmanaFund.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class SMSsinistre {

    // Get the Twilio SID and Auth Token from environment variables
    public static final String ACCOUNT_SID = System.getenv("TWILIO_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
    public static final String FROM_PHONE_NUMBER = "+18066029452"; // example: +1234567890

    public SMSsinistre() {
        // Initialize Twilio with SID and Token from environment variables
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendSms(String toPhoneNumber, String messageBody) {
        try {
            // Send the SMS via Twilio
            Message message = Message.creator(
                            new PhoneNumber(toPhoneNumber), // Recipient's phone number
                            new PhoneNumber(FROM_PHONE_NUMBER), // Your Twilio phone number
                            messageBody) // Message content
                    .create();

            System.out.println("Message sent: " + message.getSid());
        } catch (Exception e) {
            System.err.println("Error sending SMS: " + e.getMessage());
        }
    }

}
