package com.ghoul.AmanaFund.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    public static final String accountSid = "AC095115cc33bee059c417c067ccac9602";
    public static final String authToken = "9c9c22a17036a231828f7c2690972c4d";
    public static final String fromPhoneNumber = "+13372423732"; // exemple : +1234567890

    public void sendSms(String to, String messageBody) {
        Twilio.init(accountSid, authToken);
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(to),
                new com.twilio.type.PhoneNumber(fromPhoneNumber),
                messageBody
        ).create();
        System.out.println("SMS Sent: " + message.getSid());
    }
}
