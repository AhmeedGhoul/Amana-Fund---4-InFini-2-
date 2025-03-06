package com.ghoul.AmanaFund.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class SMSsinistre {

    // Remplace par tes informations Twilio
    public static final String ACCOUNT_SID = "TWILIO_SID";
    public static final String AUTH_TOKEN = "TWILIO_AUTH_TOKEN";
    public static final String FROM_PHONE_NUMBER = "+18066029452"; // exemple : +1234567890

    public SMSsinistre() {
        // Initialise Twilio avec le SID et le Token
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendSms(String toPhoneNumber, String messageBody) {
        try {
            // Envoie le SMS via Twilio
            Message message = Message.creator(
                            new PhoneNumber(toPhoneNumber), // Numéro du destinataire
                            new PhoneNumber(FROM_PHONE_NUMBER), // Numéro Twilio
                            messageBody) // Contenu du message
                    .create();

            System.out.println("Message envoyé : " + message.getSid());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du SMS : " + e.getMessage());
        }
    }

}
