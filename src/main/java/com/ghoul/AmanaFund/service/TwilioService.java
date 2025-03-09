package com.ghoul.AmanaFund.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TwilioService {

    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.auth_token}")
    private String authToken;

    @Value("${twilio.phone_number}")
    private String twilioPhoneNumber;

    @Value("${admin.phone_number}")
    private String adminPhoneNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void sendSms(String messageBody) {
        Message.creator(
                new com.twilio.type.PhoneNumber(adminPhoneNumber),  // To (Admin)
                new com.twilio.type.PhoneNumber(twilioPhoneNumber), // From (Twilio Number)
                messageBody
        ).create();
    }
}
