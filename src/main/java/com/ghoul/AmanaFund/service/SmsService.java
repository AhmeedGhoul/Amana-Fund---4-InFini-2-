package com.ghoul.AmanaFund.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class SmsService {

    @Value("AC66073f238dacc1ce82a94b28da835819")
    private String accountSid;

    @Value("145215812d7cb0ba37ba00037e421fe8")
    private String authToken;

    @Value("+13609966024")
    private String twilioPhoneNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void sendSms(String toPhoneNumber, String messageBody) {
        Message message = Message.creator(
                        new PhoneNumber(toPhoneNumber),
                        new PhoneNumber(twilioPhoneNumber),
                        messageBody)
                .create();

        System.out.println("SMS sent: " + message.getSid());
    }
}